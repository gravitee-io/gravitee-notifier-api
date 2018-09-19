/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.notifier.api;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.completedFuture;

public abstract class AbstractNotifier implements Notifier  {

    protected abstract String getType();

    private boolean canHandle(final Notification notification) {
        return getType().equals(notification.getType());
    }

    @Override
    public CompletableFuture<Void> send(final Notification notification, final Map<String, Object> parameters) {
        if (canHandle(notification)) {
            return doSend(notification, parameters);
        }
        return completedFuture(null);
    }

    protected abstract CompletableFuture<Void> doSend(final Notification notification, final Map<String, Object> parameters);
}
