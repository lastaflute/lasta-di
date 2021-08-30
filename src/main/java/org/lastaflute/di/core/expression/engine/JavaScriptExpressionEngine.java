/*
 * Copyright 2015-2021 the original author or authors.
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
package org.lastaflute.di.core.expression.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.LastaDiProperties;
import org.lastaflute.di.core.exception.ExpressionClassCreateFailureException;
import org.lastaflute.di.core.expression.dwarf.ExpressionCastResolver;
import org.lastaflute.di.core.expression.dwarf.ExpressionCastResolver.CastResolved;
import org.lastaflute.di.helper.misc.LdiExceptionMessageBuilder;
import org.lastaflute.di.util.LdiClassUtil;
import org.lastaflute.di.util.LdiStringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jflute
 */
public class JavaScriptExpressionEngine implements ExpressionEngine {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Logger logger = LoggerFactory.getLogger(JavaScriptExpressionEngine.class);

    protected static final String FIRST_ENGINE_NAME = "sai"; // forked from nashorn
    protected static final String SECOND_ENGINE_NAME = "javascript"; // means nashorn

    protected static final String SQ = "'";
    protected static final String DQ = "\"";

    // thread-safe without e.g. put, register
    protected static final ScriptEngineManager defaultManager = new ScriptEngineManager();

    // of course thread-safe
    protected static final ExpressionCastResolver castResolver = new ExpressionCastResolver();

    // ===================================================================================
    //                                                                    Parse Expression
    //                                                                    ================
    @Override
    public Object parseExpression(String source) {
        return source.trim();
    }

    // ===================================================================================
    //                                                                            Evaluate
    //                                                                            ========
    @Override
    public Object evaluate(Object exp, Map<String, ? extends Object> contextMap, LaContainer container, Class<?> resultType) {
        return viaVariableResolvedEvaluate((String) exp, contextMap, container, resultType);
    }

    protected Object viaVariableResolvedEvaluate(String exp, Map<String, ? extends Object> contextMap, LaContainer container,
            Class<?> resultType) {
        final String resolvedExp = resolveExpressionVariable(exp, contextMap);
        return viaCastResolvedEvaluate(resolvedExp, container, resultType);
    }

    protected String resolveExpressionVariable(String exp, Map<String, ? extends Object> contextMap) {
        return ExpressionEngine.resolveExpressionVariableSimply(exp, contextMap);
    }

    protected Object viaCastResolvedEvaluate(String exp, LaContainer container, Class<?> resultType) {
        final CastResolved resolved = castResolver.resolveCast(exp, resultType);
        final String realExp;
        final Class<?> realType;
        if (resolved != null) {
            realExp = resolved.getFilteredExp();
            realType = resolved.getResolvedType();
        } else {
            realExp = exp.trim();
            realType = resultType;
        }
        return viaFirstNameResolvedEvaluate(realExp, container, realType);
    }

    protected Object viaFirstNameResolvedEvaluate(String exp, LaContainer container, Class<?> resultType) {
        final String filteredExp;
        String firstName = null;
        Object firstComponent = null;
        if (!exp.startsWith(DQ) && !exp.startsWith("[") && !exp.startsWith("{") && exp.contains(".")) {
            final String componentName = exp.substring(0, exp.indexOf("."));
            final LaContainer namedContainer = container.getRoot().findChild(componentName); // in all container
            if (namedContainer != null) { // first element is named container
                final String rear = exp.substring(exp.indexOf(".") + ".".length());
                if (rear.contains(".")) { // has more chain
                    final String nextName = rear.substring(0, rear.indexOf("."));
                    if (namedContainer.hasComponentDef(nextName)) { // in named container
                        filteredExp = rear;
                        firstName = nextName;
                        firstComponent = namedContainer.getComponent(nextName);
                    } else { // may be JavaScript expression (but basically mistake...)
                        filteredExp = exp;
                        firstName = componentName;
                        firstComponent = namedContainer;
                    }
                } else {
                    if (namedContainer.hasComponentDef(rear)) { // in named container
                        return namedContainer.getComponent(rear); // resolved without evaluation
                    } else { // may be JavaScript expression (but basically mistake...)
                        filteredExp = exp;
                        firstName = componentName;
                        firstComponent = namedContainer;
                    }
                }
            } else { // first element may be component
                filteredExp = exp;
                if (container.hasComponentDef(componentName)) { // in current container only
                    firstName = componentName;
                    firstComponent = container.getComponent(componentName);
                }
            }
        } else {
            filteredExp = exp;
        }
        final Object evaluated = actuallyEvaluate(filteredExp, container, firstName, firstComponent);
        final Object filtered = filterEvaluated(filteredExp, container, evaluated, resultType);
        // needs deep thinking time for e.g. primitive, Object.class
        //checkResultTypeMatched(filtered, contextMap, container, resultType);
        return filtered;
    }

    // ===================================================================================
    //                                                                   Actually Evaluate
    //                                                                   =================
    protected Object actuallyEvaluate(String exp, LaContainer container, String firstName, Object firstComponent) {
        final ScriptEngine engine = comeOnScriptEngine(exp, container);
        if (firstName != null) {
            engine.put(firstName, firstComponent);
        }
        if (isInternalDebug()) {
            final StringBuilder sb = new StringBuilder();
            sb.append("#fw_debug ...Evaluating the script: exp={}, Di xml={}");
            if (firstName != null) {
                sb.append(", component={}/{}");
            }
            logger.debug(sb.toString(), exp, container.getPath(), firstName, firstComponent);
        }
        try {
            return engine.eval(exp);
        } catch (ScriptException | RuntimeException e) {
            throwJavaScriptExpressionException(exp, container, e);
            return null; // unreachable
        }
    }

    protected void throwJavaScriptExpressionException(String exp, LaContainer container, Exception e) {
        final LdiExceptionMessageBuilder br = new LdiExceptionMessageBuilder();
        br.addNotice("Failed to evaluate the JavaScript expression.");
        br.addItem("Expression");
        br.addElement(exp);
        br.addItem("Di XML");
        br.addElement(container.getPath());
        final String msg = br.buildExceptionMessage();
        throw new IllegalStateException(msg, e);
    }

    // ===================================================================================
    //                                                                       Script Engine
    //                                                                       =============
    // -----------------------------------------------------
    //                                            Initialize
    //                                            ----------
    // want to define this near comeOnScriptEngine() for visual check
    public void initializeManagedEngine() {
        // called by SingletonLaContainerFactory as sub thread
        // want to initialize static resources in internal world of engine
        logger.debug("#fw_debug ...Initializing framework-managed script engine for Di xml.");
        final ScriptEngineManager scriptEngineManager = prepareScriptEngineManager();
        final String specifiedName = getDiXmlScriptManagedEngineName();
        if (specifiedName != null) {
            final ScriptEngine specifiedEngine = scriptEngineManager.getEngineByName(specifiedName);
            if (isInternalDebug()) {
                if (specifiedEngine != null) {
                    logger.debug("#fw_debug Initialized the specified script engine: {}, {}", specifiedName, specifiedEngine);
                } else {
                    logger.debug("#fw_debug Not found the specified script engine: {}", specifiedName);
                }
            }
        } else {
            final ScriptEngineFound found = findEmbeddedScriptEngine(scriptEngineManager);
            if (isInternalDebug()) {
                if (found != null) {
                    final String engineName = found.getEngineName();
                    final ScriptEngine foundEngine = found.getFoundEngine();
                    logger.debug("#fw_debug Initialized the embedded script engine: {}/{}", engineName, foundEngine);
                } else {
                    logger.debug("#fw_debug Not found the embedded script engine.");
                }
            }
        }
    }

    // -----------------------------------------------------
    //                                             Come on !
    //                                             ---------
    protected ScriptEngine comeOnScriptEngine(String exp, LaContainer container) {
        // script engine is not thread safe so it should be prepared per execution 
        final ScriptEngineManager scriptEngineManager = prepareScriptEngineManager();
        final String specifyedName = getDiXmlScriptManagedEngineName();
        final ScriptEngine engine;
        if (specifyedName != null) { // specified by lasta_di.properties
            engine = scriptEngineManager.getEngineByName(specifyedName);
            if (engine == null) { // e.g. wrong name specified in lasta_di.properties
                throwScriptEngineSpecifiedNotFoundException(specifyedName, exp, container);
            }
        } else { // mainly here
            final ScriptEngineFound found = findEmbeddedScriptEngine(scriptEngineManager);
            if (found == null) { // both not found
                throwScriptEngineEmbeddedNotFoundException(exp, container);
            }
            engine = found.getFoundEngine();
        }
        return engine;
    }

    // -----------------------------------------------------
    //                                        Engine Manager
    //                                        --------------
    protected ScriptEngineManager prepareScriptEngineManager() {
        return defaultManager; // as default
    }

    // -----------------------------------------------------
    //                                      Specified Engine
    //                                      ----------------
    protected String getDiXmlScriptManagedEngineName() {
        return LastaDiProperties.getInstance().getDiXmlScriptManagedEngineName();
    }

    // -----------------------------------------------------
    //                                       Embedded Engine
    //                                       ---------------
    protected ScriptEngineFound findEmbeddedScriptEngine(ScriptEngineManager scriptEngineManager) {
        ScriptEngineFound embeddedEngine = getEngineByName(scriptEngineManager, FIRST_ENGINE_NAME);
        if (embeddedEngine == null) {
            embeddedEngine = getEngineByName(scriptEngineManager, SECOND_ENGINE_NAME);
        }
        return embeddedEngine; // null allowed
    }

    protected ScriptEngineFound getEngineByName(ScriptEngineManager scriptEngineManager, String engineName) {
        final ScriptEngine embeddedEngine = scriptEngineManager.getEngineByName(engineName);
        return embeddedEngine != null ? new ScriptEngineFound(engineName, embeddedEngine) : null;
    }

    public static class ScriptEngineFound { // to keep engine name for e.g. logging

        protected final String engineName; // not null
        protected final ScriptEngine foundEngine; // not null

        public ScriptEngineFound(String engineName, ScriptEngine foundEngine) {
            this.engineName = engineName;
            this.foundEngine = foundEngine;
        }

        @Override
        public String toString() {
            return "{" + engineName + ", " + foundEngine + "}";
        }

        public String getEngineName() {
            return engineName;
        }

        public ScriptEngine getFoundEngine() {
            return foundEngine;
        }
    }

    // -----------------------------------------------------
    //                                    Exception Handling
    //                                    ------------------
    protected void throwScriptEngineSpecifiedNotFoundException(String engineName, String exp, LaContainer container) {
        final LdiExceptionMessageBuilder br = new LdiExceptionMessageBuilder();
        br.addNotice("Not found the script engine by the specified name.");
        br.addItem("Advice");
        br.addElement("Your expression on the Di xml needs JavaScript engine.");
        br.addElement("But the script engine was not found by the engine name");
        br.addElement("specified by your lasta_di.properties.");
        br.addElement("");
        br.addElement("So confirm that the engine exists in your JavaVM.");
        br.addElement("(also your lasta_di.properties settings)");
        br.addItem("Engine Name");
        br.addElement(engineName);
        br.addItem("Script Expression");
        br.addElement(exp);
        br.addItem("Di xml");
        br.addElement(container.getPath());
        final String msg = br.buildExceptionMessage();
        throw new IllegalStateException(msg);
    }

    protected void throwScriptEngineEmbeddedNotFoundException(String exp, LaContainer container) {
        final LdiExceptionMessageBuilder br = new LdiExceptionMessageBuilder();
        br.addNotice("Not found the script engine by the embedded name.");
        br.addItem("Advice");
        br.addElement("Your expression on the Di xml needs JavaScript engine.");
        br.addElement("But Nashorn (JavaScript engine) is removed since Java15.");
        br.addElement("");
        br.addElement("So add the 'sai' library to your dependencies.");
        br.addElement("It is JavaScript engine forked from Nashorn.");
        br.addElement(" https://github.com/codelibs/sai");
        br.addItem("Script Expression");
        br.addElement(exp);
        br.addItem("Di xml");
        br.addElement(container.getPath());
        final String msg = br.buildExceptionMessage();
        throw new IllegalStateException(msg);
    }

    // ===================================================================================
    //                                                                           Filtering
    //                                                                           =========
    protected Object filterEvaluated(String exp, LaContainer container, Object evaluated, Class<?> resultType) {
        if (evaluated instanceof String) {
            // e.g. jp. cannot create the instance with this error,
            // ReferenceError: "jp" is not defined in <eval> at line number 1
            // (com. and org. can do it)
            // so you can create by quoted string expression: "new jp.dbflute.SeaLogic()"
            final String str = ((String) evaluated).trim();
            final String prefix = "new ";
            final String suffix = "()";
            if (str.startsWith(prefix) && str.endsWith(suffix)) {
                final String className = str.substring(prefix.length(), str.length() - suffix.length());
                try {
                    return LdiClassUtil.newInstance(className);
                } catch (RuntimeException e) {
                    throwExpressionClassCreateFailureException(exp, container, className, e);
                }
            }
        }
        if (evaluated instanceof Map) {
            @SuppressWarnings("unchecked")
            final Map<String, Object> map = (Map<String, Object>) evaluated;
            return handleMap(exp, container, map, resultType);
        }
        return evaluated;
    }

    protected void throwExpressionClassCreateFailureException(String exp, LaContainer container, String className, RuntimeException cause) {
        final LdiExceptionMessageBuilder br = new LdiExceptionMessageBuilder();
        br.addNotice("Failed to create the class in the expression.");
        br.addItem("Di XML");
        br.addElement(container.getPath());
        br.addItem("Expression");
        br.addElement(exp);
        br.addItem("Class Name");
        br.addElement(className);
        final String msg = br.buildExceptionMessage();
        throw new ExpressionClassCreateFailureException(msg, cause);
    }

    protected Object handleMap(String exp, LaContainer container, Map<String, Object> map, Class<?> resultType) {
        final List<Object> challengeList = challengeList(map);
        if (challengeList != null) { // e.g. [1,2] or ...
            return castResolver.convertListTo(exp, container, resultType, challengeList);
        } else {
            return map;
        }
    }

    protected List<Object> challengeList(Map<String, Object> map) {
        int index = 0;
        final Set<String> keySet = map.keySet();
        for (String key : keySet) {
            if (LdiStringUtil.isNumber(key) && Integer.parseInt(key) == index) {
                ++index;
                continue;
            }
            return null;
        }
        return new ArrayList<Object>(map.values());
    }

    // ===================================================================================
    //                                                                       Static Method
    //                                                                       =============
    @Override
    public String resolveStaticMethodReference(Class<?> refType, String methodName) {
        return refType.getName() + "." + methodName; // e.g. org.lastaflute.di.util.LdiResourceUtil.exists
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    protected boolean isInternalDebug() {
        return LastaDiProperties.getInstance().isInternalDebug();
    }
}
