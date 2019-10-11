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

import freemarker.cache.StringTemplateLoader;
import freemarker.core.TemplateClassResolver;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public abstract class AbstractConfigurableNotifier<C extends NotifierConfiguration> extends AbstractNotifier {

    protected C configuration;

    private static final Configuration CONFIGURATION;

    static {
        CONFIGURATION =
                new freemarker.template.Configuration(Configuration.VERSION_2_3_28);

        CONFIGURATION.setNewBuiltinClassResolver(TemplateClassResolver.SAFER_RESOLVER);
        CONFIGURATION.setTemplateLoader(new StringTemplateLoader());
    }

    public AbstractConfigurableNotifier(String type, C configuration) {
        super(type);
        this.configuration = configuration;
    }

    protected String templatize(String payload, Map<String, Object> parameters) throws IOException, TemplateException {
        final Template template = new Template(Integer.toString(payload.hashCode()), payload, CONFIGURATION);

        StringWriter result = new StringWriter();
        template.process(parameters, result);
        return result.toString();
    }
}
