# ð¨ éæ±åºæ¯

å¨`ThreadLocal`çéæ±åºæ¯å³æ¯`TTL`çæ½å¨éæ±åºæ¯ï¼å¦æä½ çä¸å¡éè¦ãå¨ä½¿ç¨çº¿ç¨æ± ç­ä¼æ± åå¤ç¨çº¿ç¨çç»ä»¶æåµä¸ä¼ é`ThreadLocal`ãåæ¯`TTL`ç®æ åºæ¯ã

ä¸é¢æ¯å ä¸ªå¸ååºæ¯ä¾å­ã

-------------------------------

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->


- [ð 1. åå¸å¼è·è¸ªç³»ç»](#-1-%E5%88%86%E5%B8%83%E5%BC%8F%E8%B7%9F%E8%B8%AA%E7%B3%BB%E7%BB%9F)
- [ðµ 2. æ¥å¿æ¶éè®°å½ç³»ç»ä¸ä¸æ](#-2-%E6%97%A5%E5%BF%97%E6%94%B6%E9%9B%86%E8%AE%B0%E5%BD%95%E7%B3%BB%E7%BB%9F%E4%B8%8A%E4%B8%8B%E6%96%87)
    - [`Log4j2 MDC`ç`TTL`éæ](#log4j2-mdc%E7%9A%84ttl%E9%9B%86%E6%88%90)
    - [`Logback MDC`ç`TTL`éæ](#logback-mdc%E7%9A%84ttl%E9%9B%86%E6%88%90)
- [ð 3. `Session`çº§`Cache`](#-3-session%E7%BA%A7cache)
- [ð 4. åºç¨å®¹å¨æä¸å±æ¡æ¶è·¨åºç¨ä»£ç ç»ä¸å±`SDK`ä¼ éä¿¡æ¯](#-4-%E5%BA%94%E7%94%A8%E5%AE%B9%E5%99%A8%E6%88%96%E4%B8%8A%E5%B1%82%E6%A1%86%E6%9E%B6%E8%B7%A8%E5%BA%94%E7%94%A8%E4%BB%A3%E7%A0%81%E7%BB%99%E4%B8%8B%E5%B1%82sdk%E4%BC%A0%E9%80%92%E4%BF%A1%E6%81%AF)
    - [ä¸é¢åºæ¯ä½¿ç¨`TTL`çæ´ä½ææ¶](#%E4%B8%8A%E9%9D%A2%E5%9C%BA%E6%99%AF%E4%BD%BF%E7%94%A8ttl%E7%9A%84%E6%95%B4%E4%BD%93%E6%9E%84%E6%9E%B6)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

-------------------------------

## ð 1. åå¸å¼è·è¸ªç³»ç» æ å¨é¾è·¯åæµï¼å³é¾è·¯ææ ï¼

å³äºãåå¸å¼è·è¸ªç³»ç»ãå¯ä»¥äºè§£ä¸ä¸`Google`ç`Dapper`ï¼ä»ç»çè®ºæï¼[ä¸­æ](https://bigbully.github.io/Dapper-translation/)| [è±æ](https://research.google.com/pubs/pub36356.html)ï¼ãåå¸å¼è·è¸ªç³»ç»ä½ä¸ºåºç¡è®¾æ½ï¼ä¸ä¼éå¶ãä½¿ç¨çº¿ç¨æ± ç­ä¼æ± åå¤ç¨çº¿ç¨çç»ä»¶ãï¼å¹¶ææå¯¹ä¸å¡é»è¾å°½å¯è½çéæã

åå¸å¼è·è¸ªç³»ç»çå®ç°çç¤ºæDemoåè§[`DistributedTracerUseDemo.kt`](../ttl2-compatible/src/test/java/com/alibaba/demo/distributed_tracer/refcount/DistributedTracerUseDemo.kt)

ä»ææ¯è½åä¸è®²ï¼å¨é¾è·¯åæµ ä¸ åå¸å¼è·è¸ªç³»ç» æ¯ä¸æ ·çï¼å³é¾è·¯ææ ã

PSï¼ å¤è°¢ [@wyzssw](https://github.com/https://github.com/wyzssw) å¯¹åå¸å¼è¿½è¸ªç³»ç»åºæ¯è¯´æäº¤æµåå®ç°ä¸è®¨è®ºå»ºè®®ï¼

- [Issue: åå¸å¼è¿½è¸ªç³»ç»åºæ¯ä¸ï¼å¦ä½ä½¿ç¨TTL](https://github.com/alibaba/transmittable-thread-local/issues/53)

## ðµ 2. æ¥å¿æ¶éè®°å½ç³»ç»ä¸ä¸æ

ç±äºä¸éå¶ç¨æ·åºç¨ä½¿ç¨çº¿ç¨æ± ï¼ç³»ç»çä¸ä¸æéè¦è½è·¨çº¿ç¨çä¼ éï¼ä¸ä¸å½±ååºç¨ä»£ç ã

### `Log4j2 MDC`ç`TTL`éæ

`Log4j2`éè¿[`Thread Context`](https://logging.apache.org/log4j/2.x/manual/thread-context.html)æä¾äº`Mapped Diagnostic Context`ï¼`MDC`ï¼è¯æ­ä¸ä¸æï¼çåè½ï¼éè¿[`ThreadLocal`](https://docs.oracle.com/javase/10/docs/api/java/lang/ThreadLocal.html)/[`InheritableThreadLocal`](https://docs.oracle.com/javase/10/docs/api/java/lang/InheritableThreadLocal.html)å®ç°ä¸ä¸æä¼ éã

å¨[`Thread Contextææ¡£`](https://logging.apache.org/log4j/2.x/manual/thread-context.html)ä¸­æå°äºå¨ä½¿ç¨çº¿ç¨æ± ç­ä¼æ± åå¤ç¨çº¿ç¨çç»ä»¶ï¼å¦`Executors`ï¼æ¶æé®é¢ï¼éè¦æä¾ä¸ä¸ªæºå¶æ¹æ¡ï¼

> The Stack and the Map are managed per thread and are based on ThreadLocal by default. The Map can be configured to use an InheritableThreadLocal by setting system property isThreadContextMapInheritable to "true". When configured this way, the contents of the Map will be passed to child threads. However, as discussed in the [Executors](https://docs.oracle.com/javase/10/docs/api/java/util/concurrent/Executors.html#privilegedThreadFactory%28%29) class and in other cases where thread pooling is utilized, the ThreadContext may not always be automatically passed to worker threads. In those cases the pooling mechanism should provide a means for doing so. The getContext() and cloneStack() methods can be used to obtain copies of the Map and Stack respectively.

å³æ¯`TTL`è¦è§£å³çé®é¢ï¼æä¾`Log4j2 MDC`ç`TTL`éæï¼è¯¦è§å·¥ç¨[`log4j2-ttl-thread-context-map`](https://github.com/oldratlee/log4j2-ttl-thread-context-map)ãå¯¹åºä¾èµï¼

```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>log4j2-ttl-thread-context-map</artifactId>
    <version>1.3.0</version>
</dependency>
```

å¯ä»¥å¨ [search.maven.org](https://search.maven.org/artifact/com.alibaba/log4j2-ttl-thread-context-map) æ¥çå¯ç¨ççæ¬ã

PSï¼ å¤è°¢ @bwzhang2011 å @wuwen5 å¯¹æ¥å¿åºæ¯è¯´æäº¤æµåå®ç°ä¸è®¨è®ºå»ºè®®ï¼

- [Issue: è½å¦æä¾ä¸LOG4J(2)ä¸­çMDCéææå¢å¼º](https://github.com/alibaba/transmittable-thread-local/issues/49)  [@bwzhang2011](https://github.com/bwzhang2011)
- [Issue: slf4j MDCAdapter with multi-thread-context æ¯æ](https://github.com/alibaba/transmittable-thread-local/issues/51)  [@bwzhang2011](https://github.com/bwzhang2011)

### `Logback MDC`ç`TTL`éæ

`Logback`çéæåè§[@ofpay](https://github.com/ofpay)æä¾ç[`logback-mdc-ttl`](https://github.com/ofpay/logback-mdc-ttl)ãå¯¹åºä¾èµï¼

```xml
<dependency>
    <groupId>com.ofpay</groupId>
    <artifactId>logback-mdc-ttl</artifactId>
    <version>1.0.2</version>
</dependency>
```

å¯ä»¥å¨ [search.maven.org](https://search.maven.org/artifact/com.ofpay/logback-mdc-ttl) æ¥çå¯ç¨ççæ¬ã

è¿ä¸ªéæå·²ç»å¨ **_çº¿ä¸äº§åç¯å¢_** ä½¿ç¨çãè¯´æè¯¦è§[æ¬§é£ç½çä½¿ç¨åºæ¯](https://github.com/alibaba/transmittable-thread-local/issues/73#issuecomment-300665308)ã

## ð 3. `Session`çº§`Cache`

å¯¹äºè®¡ç®é»è¾å¤æä¸å¡æµç¨ï¼åºç¡æ°æ®è¯»åæå¡ï¼è¿æ ·çè¯»åæå¡å¾å¾æ¯ä¸ªå¤é¨è¿ç¨æå¡ï¼å¯è½éè¦å¤æ¬¡è°ç¨ï¼ææè½ç¼å­èµ·æ¥ï¼ä»¥é¿åå¤æ¬¡éå¤æ§è¡é«ææ¬æä½ã

åæ¶ï¼å¨å¥å£åèµ·ä¸åçè¯·æ±ï¼å¤ççæ¯ä¸åç¨æ·çæ°æ®ï¼æä»¥ä¸ååèµ·è¯·æ±ä¹é´ä¸éè¦å±äº«æ°æ®ï¼è¿æ ·ä¹è½é¿åè¯·æ±å¯¹åºçä¸åç¨æ·ä¹é´å¯è½çæ°æ®æ±¡æã

å ä¸ºæ¶åå¤ä¸ªä¸ä¸æ¸¸çº¿ç¨ï¼å¶å®æ¯`Session`çº§ç¼å­ã

éè¿`Session`çº§ç¼å­å¯ä»¥

- é¿åéå¤æ§è¡é«ææ¬æä½ï¼æåæ§è½ã
- é¿åä¸å`Session`ä¹é´çæ°æ®æ±¡æã

æ´å¤è®¨è®ºä¸ä½¿ç¨æ¹å¼åè§[**_`@olove`_**](https://github.com/olove) æçIssueï¼[è®¨è®ºï¼Sessionçº§Cacheåºæ¯ä¸ï¼TransmittableThreadLocalçä½¿ç¨](https://github.com/alibaba/transmittable-thread-local/issues/122)ã

## ð 4. åºç¨å®¹å¨æä¸å±æ¡æ¶è·¨åºç¨ä»£ç ç»ä¸å±`SDK`ä¼ éä¿¡æ¯

ä¸¾ä¸ªå·ä½çä¸å¡åºæ¯ï¼å¨`App Engine`ï¼`PAAS`ï¼ä¸ä¼è¿è¡ç±åºç¨æä¾åæä¾çåºç¨ï¼`SAAS`æ¨¡å¼ï¼ãå¤ä¸ª`SAAS`ç¨æ·è´­ä¹°å¹¶ä½¿ç¨è¿ä¸ªåºç¨ï¼å³`SAAS`åºç¨ï¼ã`SAAS`åºç¨å¾å¾æ¯ä¸ä¸ªå®ä¾ä¸ºå¤ä¸ª`SAAS`ç¨æ·æä¾æå¡ã  
\# å¦ä¸ç§æ¨¡å¼æ¯ï¼`SAAS`ç¨æ·ä½¿ç¨å®å¨ç¬ç«ä¸ä¸ª`SAAS`åºç¨ï¼åå«ç¬ç«åºç¨å®ä¾åå¶åçæ°æ®æºï¼å¦`DB`ãç¼å­ï¼etcï¼ã

éè¦é¿åç`SAAS`åºç¨æ¿å°å¤ä¸ª`SAAS`ç¨æ·çæ°æ®ãä¸ä¸ªè§£å³æ¹æ³æ¯å¤çè¿ç¨å³èå¥½ä¸ä¸ª`SAAS`ç¨æ·çä¸ä¸æï¼å¨ä¸ä¸æä¸­åºç¨åªè½å¤çï¼è¯»/åï¼è¿ä¸ª`SAAS`ç¨æ·çæ°æ®ãè¯·æ±ç±`SAAS`ç¨æ·åèµ·ï¼å¦ä»`Web`è¯·æ±è¿å¥`App Engine`ï¼ï¼`App Engine`å¯ä»¥ç¥éæ¯ä»åªä¸ª`SAAS`ç¨æ·ï¼å¨`Web`è¯·æ±æ¶å¨ä¸ä¸æä¸­è®¾ç½®å¥½`SAAS`ç¨æ·`ID`ãåºç¨å¤çæ°æ®ï¼`DB`ã`Web`ãæ¶æ¯ etc.ï¼æ¯éè¿`App Engine`æä¾çæå¡`SDK`æ¥å®æãå½åºç¨å¤çæ°æ®æ¶ï¼`SDK`æ£æ¥æ°æ®æå±ç`SAAS`ç¨æ·æ¯å¦åä¸ä¸æä¸­ç`SAAS`ç¨æ·`ID`ä¸è´ï¼å¦æä¸ä¸è´åæç»æ°æ®çè¯»åã

åºç¨ä»£ç ä¼ä½¿ç¨çº¿ç¨æ± ï¼å¹¶ä¸è¿æ ·çä½¿ç¨æ¯æ­£å¸¸çä¸å¡éæ±ã`SAAS`ç¨æ·`ID`çä»è¦`App Engine`ä¼ éå°ä¸å±`SDK`ï¼è¦æ¯æè¿æ ·çç¨æ³ã

### ä¸é¢åºæ¯ä½¿ç¨`TTL`çæ´ä½ææ¶

<img src="scenario-framework-sdk-arch.png" alt="ææ¶å¾" width="260" />

ææ¶æ¶å3ä¸ªè§è²ï¼å®¹å¨ãç¨æ·åºç¨ã`SDK`ã

æ´ä½æµç¨ï¼

1. è¯·æ±è¿å¥`PAAS`å®¹å¨ï¼æåä¸ä¸æä¿¡æ¯å¹¶è®¾ç½®å¥½ä¸ä¸æã
2. è¿å¥ç¨æ·åºç¨å¤çä¸å¡ï¼ä¸å¡è°ç¨`SDK`ï¼å¦`DB`ãæ¶æ¯ãetcï¼ã  
    ç¨æ·åºç¨ä¼ä½¿ç¨çº¿ç¨æ± ï¼æä»¥è°ç¨`SDK`ççº¿ç¨å¯è½ä¸æ¯è¯·æ±ççº¿ç¨ã
3. è¿å¥`SDK`å¤çã  
    æåä¸ä¸æçä¿¡æ¯ï¼å³å®æ¯å¦ç¬¦åæç»å¤çã

æ´ä¸ªè¿ç¨ä¸­ï¼ä¸ä¸æçä¼ é å¯¹äº **ç¨æ·åºç¨ä»£ç ** æææ¯éæçã
