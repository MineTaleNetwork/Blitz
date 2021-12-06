package cc.minetale.blitz.manager;

import cc.minetale.blitz.api.BlitzPlayer;
import cc.minetale.commonlib.profile.Profile;
import cc.minetale.commonlib.profile.ProfileQueryResult;
import cc.minetale.commonlib.util.Database;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import org.bson.Document;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Getter
public class PlayerManager {

    @Getter private static PlayerManager playerManager;
    private final Cache<UUID, BlitzPlayer> cache;

    public PlayerManager(CacheManager cacheManager) {
        PlayerManager.playerManager = this;

        this.cache = cacheManager.createCache("players",
                CacheConfigurationBuilder.newCacheConfigurationBuilder(UUID.class, BlitzPlayer.class,
                        ResourcePoolsBuilder.newResourcePoolsBuilder().heap(100000, EntryUnit.ENTRIES)));
    }

    public CompletableFuture<ProfileQueryResult> createProfile(String name, UUID uuid) {
        return createProfile(new Profile(name, uuid));
    }

    public CompletableFuture<ProfileQueryResult> createProfile(Profile profile) {
        return new CompletableFuture<ProfileQueryResult>()
                .completeAsync(() -> {
                    try {
                        if(getProfile(profile.getId()).get() != null) { return ProfileQueryResult.PROFILE_EXISTS; }
                        updateProfile(profile);
                        return ProfileQueryResult.CREATED_PROFILE;
                    } catch(InterruptedException | ExecutionException e) {
                        Thread.currentThread().interrupt();
                        e.printStackTrace();
                    }
                    return ProfileQueryResult.FAILURE;
                });
    }

    public void updateProfile(Profile profile) {
        var player = this.cache.get(profile.getId());

        if(player != null) {
            player.setProfile(profile);
        } else {
            this.cache.put(profile.getId(), new BlitzPlayer(profile));
        }

        Database.getDatabase().getProfilesCollection().replaceOne(Filters.eq(profile.getId().toString()), profile.toDocument(), new ReplaceOptions().upsert(true));
    }

    public CompletableFuture<Profile> getProfile(String name) {
        return new CompletableFuture<Profile>()
                .completeAsync(() -> {
                    for(Cache.Entry<UUID, BlitzPlayer> ent : this.cache) {
                        var player = ent.getValue();
                        if(player.getName().equalsIgnoreCase(name))
                            return player.getProfile();
                    }

                    var document = Database.getDatabase().getProfilesCollection().find(Filters.eq("search", name.toUpperCase())).first();
                    if(document == null) { return null; }

                    var profile = Profile.fromDocument(document);
                    var player = this.cache.get(profile.getId());

                    if(player != null) {
                        player.setProfile(profile);
                    } else {
                        this.cache.put(profile.getId(), new BlitzPlayer(profile));
                    }

                    return profile;
                });
    }

    public CompletableFuture<Profile> getProfile(UUID uuid) {
        return new CompletableFuture<Profile>()
                .completeAsync(() -> {
                    var player = this.cache.get(uuid);
                    if(player != null) { return player.getProfile(); }

                    var document = Database.getDatabase().getProfilesCollection().find(Filters.eq(uuid.toString())).first();
                    if(document == null) { return null; }

                    var profile = Profile.fromDocument(document);

                    if(profile == null) { return null; }

                    player = this.cache.get(profile.getId());

                    if(player != null) {
                        player.setProfile(profile);
                    } else {
                        this.cache.put(profile.getId(), new BlitzPlayer(profile));
                    }

                    return profile;
                });
    }

    public CompletableFuture<List<Profile>> getProfilesByNames(List<String> names) {
        return new CompletableFuture<List<Profile>>()
                .completeAsync(() -> {
                    List<Profile> profiles = new ArrayList<>();

                    List<String> searchable = names.stream()
                            .map(String::toUpperCase)
                            .collect(Collectors.toList());

                    for(Cache.Entry<UUID, BlitzPlayer> ent : this.cache) {
                        var player = ent.getValue();
                        for(final var it = searchable.iterator(); it.hasNext();) {
                            var name = it.next();
                            if(name == null || name.isEmpty()) { continue; }

                            if(player.getName().equals(name)) {
                                it.remove();
                                profiles.add(player.getProfile());
                            }
                        }
                    }

                    var documents = Database.getDatabase().getProfilesCollection().find(Filters.in("search", searchable));

                    return getProfiles(profiles, documents);
                });
    }

    private List<Profile> getProfiles(List<Profile> profiles, FindIterable<Document> documents) {
        try(MongoCursor<Document> cursor = documents.cursor()) {
            while(cursor.hasNext()) {
                var document = cursor.next();

                var profile = Profile.fromDocument(document);
                var player = this.cache.get(profile.getId());

                if(player != null) {
                    player.setProfile(profile);
                } else {
                    this.cache.put(profile.getId(), new BlitzPlayer(profile));
                }
            }
        }

        return profiles;
    }

    public CompletableFuture<List<Profile>> getProfilesByIds(List<UUID> ids) {
        return new CompletableFuture<List<Profile>>()
                .completeAsync(() -> {
                    List<Profile> profiles = new ArrayList<>();

                    for (Cache.Entry<UUID, BlitzPlayer> ent : this.cache) {
                        var player = ent.getValue();
                        for (final var it = ids.iterator(); it.hasNext(); ) {
                            var id = it.next();
                            if (id == null) {
                                continue;
                            }

                            if (player.getUniqueId().equals(id)) {
                                it.remove();
                                profiles.add(player.getProfile());
                            }
                        }
                    }

                    List<String> searchable = ids.stream()
                            .map(UUID::toString)
                            .collect(Collectors.toList());

                    var documents = Database.getDatabase().getProfilesCollection().find(Filters.in("_id", searchable));

                    return getProfiles(profiles, documents);
                });
    }

}
