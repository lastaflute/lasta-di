# Lasta Di
DI Container for LastaFlute, forked from Seasar as Java8

# Two components
- Quick component: convention registration, hot reloading (prototype)
- Rich component: flexible manual registration (singleton)

## How to inject
Both components can be injected by @Resource annotation.
```java
@Resource
private AbcLogic abcLogic;
```

# Speedy Boot
Simple logic in Lasta Di, speed is prior.
And following features:
- Small dependencies: Javassit, JTA, Slf4j (only three)
- Lazy Loading: Minimum initialization in (in hot, warm)

# Quick Trial
Can boot it by example of LastaFlute:

1. prepare Java8 compile environment
2. clone https://github.com/dbflute-session/lastaflute-example-harbor
3. execute the main method of (org.docksidestage.boot) HarborBoot
4. access to http://localhost:8090/harbor

*you can login by user 'Pixy' and password 'sea', and can see debug log at console

# Maven Dependency
```xml
<dependency>
    <groupId>org.lastaflute</groupId>
    <artifactId>lasta-di</artifactId>
    <version>0.6.1</version>
</dependency>
```

# Japanese Site (English comming soon...)
http://dbflute.seasar.org/ja/lastaflute/lastadi/

# Thanks, Friends
Not only LastaFlute, Lasta Di is used by:
- RiverWeb: https://github.com/codelibs/elasticsearch-river-web
- S2Robot: https://github.com/codelibs/s2robot
