/*
 * Copyright 2015-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.lastaflute.di.core.util;

import java.util.HashSet;
import java.util.Set;

import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.LaContainer;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class Traversal {

    public static interface S2ContainerHandler {
        Object processContainer(LaContainer container);
    }

    public static interface ComponentDefHandler {
        Object processComponent(ComponentDef componentDef);
    }

    public static Object forEachComponent(final LaContainer container, final ComponentDefHandler handler) {
        return forEachComponent(container, handler, true);
    }

    public static Object forEachComponent(final LaContainer container, final ComponentDefHandler handler, final boolean parentFirst) {
        return forEachContainer(container, new S2ContainerHandler() {
            public Object processContainer(final LaContainer container) {
                for (int i = 0; i < container.getComponentDefSize(); ++i) {
                    final Object result = handler.processComponent(container.getComponentDef(i));
                    if (result != null) {
                        return result;
                    }
                }
                return null;
            }
        }, parentFirst);
    }

    public static Object forEachContainer(final LaContainer container, final S2ContainerHandler handler) {
        return forEachContainer(container, handler, true, new HashSet<LaContainer>());
    }

    public static Object forEachContainer(final LaContainer container, final S2ContainerHandler handler, final boolean parentFirst) {
        return forEachContainer(container, handler, parentFirst, new HashSet<LaContainer>());
    }

    protected static Object forEachContainer(final LaContainer container, final S2ContainerHandler handler, final boolean parentFirst,
            final Set<LaContainer> processed) {
        if (parentFirst) {
            final Object result = handler.processContainer(container);
            if (result != null) {
                return result;
            }
        }
        for (int i = 0; i < container.getChildSize(); ++i) {
            final LaContainer child = container.getChild(i);
            if (processed.contains(child)) {
                continue;
            }
            processed.add(child);
            final Object result = forEachContainer(child, handler, parentFirst, processed);
            if (result != null) {
                return result;
            }
        }
        if (!parentFirst) {
            return handler.processContainer(container);
        }
        return null;
    }

    public static Object forEachParentContainer(final LaContainer container, final S2ContainerHandler handler) {
        return forEachParentContainer(container, handler, true, new HashSet<LaContainer>());
    }

    public static Object forEachParentContainer(final LaContainer container, final S2ContainerHandler handler, final boolean childFirst) {
        return forEachParentContainer(container, handler, childFirst, new HashSet<LaContainer>());
    }

    protected static Object forEachParentContainer(final LaContainer container, final S2ContainerHandler handler, final boolean childFirst,
            final Set<LaContainer> processed) {
        if (childFirst) {
            final Object result = handler.processContainer(container);
            if (result != null) {
                return result;
            }
        }
        for (int i = 0; i < container.getParentSize(); ++i) {
            final LaContainer parent = container.getParent(i);
            if (processed.contains(parent)) {
                continue;
            }
            processed.add(parent);
            final Object result = forEachParentContainer(parent, handler, childFirst, processed);
            if (result != null) {
                return result;
            }
        }
        if (!childFirst) {
            return handler.processContainer(container);
        }
        return null;
    }
}
