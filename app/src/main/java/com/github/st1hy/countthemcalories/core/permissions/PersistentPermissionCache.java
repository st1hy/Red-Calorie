package com.github.st1hy.countthemcalories.core.permissions;

import com.google.common.collect.ForwardingMap;
import com.google.common.collect.Maps;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;


/**
 * Stores information about result of permission check if user has been asked directly to
 * enable permission. Results should be taken with a grain of salt, since user could have revoked
 * them in other activity. With multi-window maybe even when app is running (?)
 */

@Singleton
public class PersistentPermissionCache extends ForwardingMap<String, Permission> {

    final Map<String, Permission> cache = Maps.newConcurrentMap();

    @Inject
    public PersistentPermissionCache() {
    }

    @Override
    protected Map<String, Permission> delegate() {
        return cache;
    }
}
