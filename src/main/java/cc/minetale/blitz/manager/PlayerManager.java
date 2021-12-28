//package cc.minetale.blitz.manager;
//
//import cc.minetale.blitz.Blitz;
//import cc.minetale.blitz.api.BlitzPlayer;
//import cc.minetale.blitz.api.StaffMembers;
//import cc.minetale.blitz.timers.GrantTimer;
//import cc.minetale.commonlib.api.Profile;
//import cc.minetale.commonlib.api.ProfileQueryResult;
//import cc.minetale.commonlib.api.Rank;
//import cc.minetale.commonlib.pigeon.payloads.profile.ProfileUpdatePayload;
//import cc.minetale.commonlib.util.Database;
//import cc.minetale.commonlib.util.PigeonUtil;
//import com.mongodb.client.FindIterable;
//import com.mongodb.client.model.Filters;
//import com.mongodb.client.model.ReplaceOptions;
//import lombok.Getter;
//import org.bson.Document;
//import org.ehcache.Cache;
//import org.ehcache.config.builders.CacheConfigurationBuilder;
//import org.ehcache.config.builders.CacheManagerBuilder;
//import org.ehcache.config.builders.ResourcePoolsBuilder;
//import org.ehcache.config.units.EntryUnit;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.ExecutionException;
//import java.util.stream.Collectors;
//
//public class PlayerManager {
//
//    @Getter private static final Cache<UUID, BlitzPlayer> cache = CacheManagerBuilder.newCacheManagerBuilder()
//            .build(true)
//            .createCache("players", CacheConfigurationBuilder.newCacheConfigurationBuilder(
//                    UUID.class,
//                    BlitzPlayer.class,
//                    ResourcePoolsBuilder.newResourcePoolsBuilder().heap(100000, EntryUnit.ENTRIES)
//            ));
//
//    public static void createTimers(UUID uuid) {
//        getProfile(uuid).thenAccept(PlayerManager::createTimers);
//    }
//
//    public static void createTimers(Profile profile) {
//        var timer = Blitz.getBlitz().getTimerManager();
//        var uuid = profile.getId();
//
//        for(var grant : profile.getCachedGrants()) {
//            if(!grant.isPermanent() && grant.isActive()) {
//                System.out.println(grant.getMillisRemaining());
//
//                timer.addTimer(new GrantTimer(grant.getId(), uuid, grant.getMillisRemaining()));
//            }
//        }
//
//        // TODO -> Punishments
////        for(var grant : profile.getCachedGrants()) {
////            if(!grant.isPermanent() && grant.isActive()) {
////                timer.addTimer(new GrantTimer(grant.getId(), uuid, grant.getMillisRemaining()));
////            }
////        }
//    }
//
//    public static CompletableFuture<ProfileQueryResult> createProfile(String name, UUID uuid) {
//        return createProfile(Profile.createBlitzProfile(uuid, name));
//    }
//
//    public static CompletableFuture<ProfileQueryResult> createProfile(Profile profile) {
//        return new CompletableFuture<ProfileQueryResult>()
//                .completeAsync(() -> {
//                    try {
//                        if(getProfile(profile.getUuid()).get() != null) { return ProfileQueryResult.PROFILE_EXISTS; }
//                        saveProfile(profile);
//                        return ProfileQueryResult.CREATED_PROFILE;
//                    } catch(InterruptedException | ExecutionException e) {
//                        Thread.currentThread().interrupt();
//                        e.printStackTrace();
//                    }
//
//                    return ProfileQueryResult.FAILURE;
//                });
//    }
//
//    public static void updateProfile(Profile profile) {
//        var player = cache.get(profile.getId());
//
//        if(player != null) {
//            player.setProfile(profile);
//        } else {
//            player = new BlitzPlayer(profile);
//            cache.put(profile.getId(), player);
//        }
//
//        if (Rank.hasMinimumRank(profile, Rank.HELPER) && player.isOnline()) {
//            StaffMembers.addMember(player);
//        } else {
//            StaffMembers.removeMember(player);
//        }
//
//        profile.validate();
//        saveProfile(profile);
//
//        PigeonUtil.broadcast(new ProfileUpdatePayload(profile, result -> {}));
//    }
//
//    public static void saveProfile(Profile profile) {
//        Database.getProfilesCollection().replaceOne(Filters.eq(profile.getId().toString()), profile.toDocument(), new ReplaceOptions().upsert(true));
//    }
//
//    public static CompletableFuture<Profile> getProfile(String name) {
//        return new CompletableFuture<Profile>()
//                .completeAsync(() -> {
//                    for(Cache.Entry<UUID, BlitzPlayer> ent : cache) {
//                        var player = ent.getValue();
//
//                        if(player.getName().equalsIgnoreCase(name))
//                            return player.getProfile();
//                    }
//
//                    var document = Database.getProfilesCollection().find(Filters.eq("search", name.toUpperCase())).first();
//                    if(document == null) { return null; }
//
//                    var profile = Profile.fromDocument(document);
//                    var player = cache.get(profile.getId());
//
//                    if(player != null) {
//                        player.setProfile(profile);
//                    } else {
//                        cache.put(profile.getId(), new BlitzPlayer(profile));
//                    }
//
//                    return profile;
//                });
//    }
//
//    public static CompletableFuture<Profile> getProfile(UUID uuid) {
//        return new CompletableFuture<Profile>()
//                .completeAsync(() -> {
//                    var player = cache.get(uuid);
//                    if(player != null) { return player.getProfile(); }
//
//                    var document = Database.getProfilesCollection().find(Filters.eq(uuid.toString())).first();
//                    if(document == null) { return null; }
//
//                    var profile = Profile.fromDocument(document);
//
//                    player = cache.get(profile.getId());
//
//                    if(player != null) {
//                        player.setProfile(profile);
//
//                        System.out.println(profile.getGrant().toString());
//                    } else {
//                        cache.put(profile.getId(), new BlitzPlayer(profile));
//                    }
//
//                    return profile;
//                });
//    }
//
////    public static CompletableFuture<List<Profile>> getProfilesByNames(List<String> names) {
////        return new CompletableFuture<List<Profile>>()
////                .completeAsync(() -> {
////                    List<Profile> profiles = new ArrayList<>();
////
////                    List<String> searchable = names.stream()
////                            .map(String::toUpperCase)
////                            .collect(Collectors.toList());
////
////                    for(Cache.Entry<UUID, BlitzPlayer> ent : cache) {
////                        var player = ent.getValue();
////                        for(final var it = searchable.iterator(); it.hasNext();) {
////                            var name = it.next();
////                            if(name == null || name.isEmpty()) { continue; }
////
////                            if(player.getName().equals(name)) {
////                                it.remove();
////                                profiles.add(player.getProfile());
////                            }
////                        }
////                    }
////
////                    var documents = Database.getProfilesCollection().find(Filters.in("search", searchable));
////
////                    return getProfiles(profiles, documents);
////                });
////    }
////
////    private static List<Profile> getProfiles(List<Profile> profiles, FindIterable<Document> documents) {
////        try(var cursor = documents.cursor()) {
////            while(cursor.hasNext()) {
////                var document = cursor.next();
////
////                var profile = Profile.fromDocument(document);
////                var player = cache.get(profile.getId());
////
////                if(player != null) {
////                    player.setProfile(profile);
////                } else {
////                    cache.put(profile.getId(), new BlitzPlayer(profile));
////                }
////            }
////        }
////
////        return profiles;
////    }
////
////    public static CompletableFuture<List<Profile>> getProfilesByIds(List<UUID> ids) {
////        return new CompletableFuture<List<Profile>>()
////                .completeAsync(() -> {
////                    List<Profile> profiles = new ArrayList<>();
////
////                    for (Cache.Entry<UUID, BlitzPlayer> ent : cache) {
////                        var player = ent.getValue();
////                        for (final var it = ids.iterator(); it.hasNext(); ) {
////                            var id = it.next();
////                            if (id == null) {
////                                continue;
////                            }
////
////                            if (player.getUniqueId().equals(id)) {
////                                it.remove();
////                                profiles.add(player.getProfile());
////                            }
////                        }
////                    }
////
////                    List<String> searchable = ids.stream()
////                            .map(UUID::toString)
////                            .collect(Collectors.toList());
////
////                    var documents = Database.getProfilesCollection().find(Filters.in("_id", searchable));
////
////                    return getProfiles(profiles, documents);
////                });
////    }
//
//}
