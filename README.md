Lasta Di
=======================
simple DI container for LastaFlute, forked from Seasar as Java8

```java
@Resource
private AbcLogic abcLogic;
```

## Two components
- Quick component: convention registration, hot reloading (prototype)
- Rich component: flexible manual registration (singleton)

## Speedy Boot
Simple logic in Lasta Di, speed is prior.
And following features:
- Small dependencies: Javassit, JTA, Slf4j (only three)
- Lazy Loading: Minimum initialization in (in hot, warm)

# Quick Trial
Can boot it by example of LastaFlute:

1. git clone https://github.com/lastaflute/lastaflute-example-harbor.git
2. prepare database by *ReplaceSchema at DBFlute client directory 'dbflute_maihamadb'  
3. compile it by Java8, on e.g. Eclipse or IntelliJ or ... as Maven project
4. execute the *main() method of (org.docksidestage.boot) HarborBoot
5. access to http://localhost:8090/harbor  
and login by user 'Pixy' and password 'sea', and can see debug log at console.

*ReplaceSchema
```java
// call manage.sh at lastaflute-example-harbor/dbflute_maihamadb
// and select replace-schema in displayed menu
...$ sh manage.sh
```

*main() method
```java
public class HarborBoot {

    public static void main(String[] args) {
        new JettyBoot(8090, "/harbor").asDevelopment().bootAwait();
    }
}
```

# Information
## Maven Dependency in pom.xml
```xml
<dependency>
    <groupId>org.lastaflute</groupId>
    <artifactId>lasta-di</artifactId>
    <version>0.9.1</version>
</dependency>
```

## Official site
(English pages have a low count but are increscent...)  
http://dbflute.seasar.org/lastaflute/lastadi/

# Thanks, Frameworks
Lasta Di forks Seasar, AOP alliance and extends it, thankful wonderful functions.  
And also forks S2ClassBuilder (called Redefiner in Lasta Di), provides flexible Di xml.  
If the frameworks were not there, no Lasta Di here.

I appreciate every framework.

# Thanks, Friends
Not only LastaFlute, Lasta Di is used by:
- RiverWeb: https://github.com/codelibs/elasticsearch-river-web
- S2Robot: https://github.com/codelibs/s2robot

Deeply Thanks!
