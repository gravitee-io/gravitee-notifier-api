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

import static java.util.concurrent.CompletableFuture.completedFuture;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public abstract class AbstractNotifier implements Notifier {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String type;

    AbstractNotifier(String type) {
        this.type = type;
    }

    private boolean canHandle(final Notification notification) {
        return type.equals(notification.getType());
    }

    @Override
    public CompletableFuture<Void> send(final Notification notification, final Map<String, Object> parameters) {
        if (canHandle(notification)) {
            return doSend(notification, parameters);
        }

        return completedFuture(null);
    }

    private String getType() {
        return type;
    }

    public String name() {
        return null;
    }

    protected abstract CompletableFuture<Void> doSend(final Notification notification, final Map<String, Object> parameters);
}
