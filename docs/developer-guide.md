# ð Developer Guide

---------------------------

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->

- [ð æ¡æ¶/ä¸­é´ä»¶éæ`TTL`ä¼ é](#-%E6%A1%86%E6%9E%B6%E4%B8%AD%E9%97%B4%E4%BB%B6%E9%9B%86%E6%88%90ttl%E4%BC%A0%E9%80%92)
- [ð å³äº`Java Agent`](#-%E5%85%B3%E4%BA%8Ejava-agent)
    - [`Java Agent`æ¹å¼å¯¹åºç¨ä»£ç æ ä¾µå¥](#java-agent%E6%96%B9%E5%BC%8F%E5%AF%B9%E5%BA%94%E7%94%A8%E4%BB%A3%E7%A0%81%E6%97%A0%E4%BE%B5%E5%85%A5)
    - [å·²æ`Java Agent`ä¸­åµå¥`TTL Agent`](#%E5%B7%B2%E6%9C%89java-agent%E4%B8%AD%E5%B5%8C%E5%85%A5ttl-agent)
- [ð¢ `Bootstrap ClassPath`ä¸æ·»å éç¨åº`Jar`çé®é¢åå¶è§£å³æ¹æ³](#-bootstrap-classpath%E4%B8%8A%E6%B7%BB%E5%8A%A0%E9%80%9A%E7%94%A8%E5%BA%93jar%E7%9A%84%E9%97%AE%E9%A2%98%E5%8F%8A%E5%85%B6%E8%A7%A3%E5%86%B3%E6%96%B9%E6%B3%95)
- [ð¨ å¦ä½ç¼è¯æå»º](#-%E5%A6%82%E4%BD%95%E7%BC%96%E8%AF%91%E6%9E%84%E5%BB%BA)
- [åå¸æä½åè¡¨](#%E5%8F%91%E5%B8%83%E6%93%8D%E4%BD%9C%E5%88%97%E8%A1%A8)
- [ð ç¸å³èµæ](#-%E7%9B%B8%E5%85%B3%E8%B5%84%E6%96%99)
    - [`JDK` core classes](#jdk-core-classes)
    - [`Java Agent`](#java-agent)
    - [`Javassist`](#javassist)
    - [`Maven Shade`æä»¶](#maven-shade%E6%8F%92%E4%BB%B6)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

---------------------------

# ð æ¡æ¶/ä¸­é´ä»¶éæ`TTL`ä¼ é

æ¡æ¶/ä¸­é´ä»¶éæ`TTL`ä¼ éï¼éè¿[`TransmittableThreadLocal.Transmitter`](../ttl-core/src/main/java/com/alibaba/ttl3/transmitter/Transmitter.java)
æåå½åçº¿ç¨çææ`TTL`å¼å¹¶å¨å¶ä»çº¿ç¨è¿è¡åæ¾ï¼å¨åæ¾çº¿ç¨æ§è¡å®ä¸å¡æä½åï¼æ¢å¤ä¸ºåæ¾çº¿ç¨åæ¥ç`TTL`å¼ã

`TransmittableThreadLocal.Transmitter`æä¾äºææ`TTL`å¼çæåãåæ¾åæ¢å¤æ¹æ³ï¼å³`CRR`æä½ï¼ï¼

1. `capture`æ¹æ³ï¼æåçº¿ç¨ï¼çº¿ç¨Aï¼çææ`TTL`å¼ã
2. `replay`æ¹æ³ï¼å¨å¦ä¸ä¸ªçº¿ç¨ï¼çº¿ç¨Bï¼ä¸­ï¼åæ¾å¨`capture`æ¹æ³ä¸­æåç`TTL`å¼ï¼å¹¶è¿å åæ¾å`TTL`å¼çå¤ä»½
3. `restore`æ¹æ³ï¼æ¢å¤çº¿ç¨Bæ§è¡`replay`æ¹æ³ä¹åç`TTL`å¼ï¼å³å¤ä»½ï¼

ç¤ºä¾ä»£ç ï¼

```java
// ===========================================================================
// çº¿ç¨ A
// ===========================================================================

TransmittableThreadLocal<String> context = new TransmittableThreadLocal<>();
context.set("value-set-in-parent");

// (1) æåå½åçº¿ç¨çææTTLå¼
final Object captured = TransmittableThreadLocal.Transmitter.capture();

// ===========================================================================
// çº¿ç¨ Bï¼å¼æ­¥çº¿ç¨ï¼
// ===========================================================================

// (2) å¨çº¿ç¨ Bä¸­åæ¾å¨captureæ¹æ³ä¸­æåçTTLå¼ï¼å¹¶è¿å åæ¾åTTLå¼çå¤ä»½
final Object backup = TransmittableThreadLocal.Transmitter.replay(captured);
try {
    // ä½ çä¸å¡é»è¾ï¼è¿éä½ å¯ä»¥è·åå°å¤é¢è®¾ç½®çTTLå¼
    String value = context.get();

    System.out.println("Hello: " + value);
    ...
    String result = "World: " + value;
} finally {
    // (3) æ¢å¤çº¿ç¨ Bæ§è¡replayæ¹æ³ä¹åçTTLå¼ï¼å³å¤ä»½ï¼
    TransmittableThreadLocal.Transmitter.restore(backup);
}
```

æ´å¤`TTL`ä¼ éçä»£ç å®ç°ç¤ºä¾ï¼åè§ [`TtlRunnable.java`](../ttl-core/src/main/java/com/alibaba/ttl3/TtlRunnable.java)ã[`TtlCallable.java`](../ttl-core/src/main/java/com/alibaba/ttl3/TtlCallable.java)ã

å½ç¶å¯ä»¥ä½¿ç¨`TransmittableThreadLocal.Transmitter`çå·¥å·æ¹æ³`runSupplierWithCaptured`å`runCallableWithCaptured`åå¯ç±ç`Java 8 Lambda`è¯­æ³
æ¥ç®å`replay`å`restore`æä½ï¼ç¤ºä¾ä»£ç ï¼

```java
// ===========================================================================
// çº¿ç¨ A
// ===========================================================================

TransmittableThreadLocal<String> context = new TransmittableThreadLocal<>();
context.set("value-set-in-parent");

// (1) æåå½åçº¿ç¨çææTTLå¼
final Object captured = TransmittableThreadLocal.Transmitter.capture();

// ===========================================================================
// çº¿ç¨ Bï¼å¼æ­¥çº¿ç¨ï¼
// ===========================================================================

String result = runSupplierWithCaptured(captured, () -> {
    // ä½ çä¸å¡é»è¾ï¼è¿éä½ å¯ä»¥è·åå°å¤é¢è®¾ç½®çTTLå¼
    String value = context.get();
    System.out.println("Hello: " + value);
    ...
    return "World: " + value;
}); // (2) + (3)
```

- æ´å¤`TTL`ä¼ éçè¯´æï¼è¯¦è§[`TransmittableThreadLocal.Transmitter`ç`JavaDoc`](../ttl-core/src/main/java/com/alibaba/ttl3/transmitter/Transmitter.java)ã
- æ´å¤`TTL`ä¼ éçä»£ç å®ç°ï¼åè§[`TtlRunnable.java`](../ttl-core/src/main/java/com/alibaba/ttl3/TtlRunnable.java)ã[`TtlCallable.java`](../ttl-core/src/main/java/com/alibaba/ttl3/TtlCallable.java)ã

# ð å³äº`Java Agent`

## `Java Agent`æ¹å¼å¯¹åºç¨ä»£ç æ ä¾µå¥

[User Guide - 2.3 ä½¿ç¨`Java Agent`æ¥ä¿®é¥°`JDK`çº¿ç¨æ± å®ç°ç±»](../README.md#23-%E4%BD%BF%E7%94%A8java-agent%E6%9D%A5%E4%BF%AE%E9%A5%B0jdk%E7%BA%BF%E7%A8%8B%E6%B1%A0%E5%AE%9E%E7%8E%B0%E7%B1%BB) è¯´å°äºï¼ç¸å¯¹ä¿®é¥°`Runnable`ææ¯çº¿ç¨æ± çæ¹å¼ï¼`Java Agent`æ¹å¼æ¯å¯¹åºç¨ä»£ç æ ä¾µå¥çãä¸é¢åä¸äºå±å¼è¯´æã

<img src="scenario-framework-sdk-arch.png" alt="ææ¶å¾" width="260" />

ææ¡æ¶å¾ï¼æåé¢ç¤ºä¾ä»£ç æä½å¯ä»¥åæä¸é¢å é¨åï¼

1. è¯»åä¿¡æ¯è®¾ç½®å°`TTL`ã  
    è¿é¨åå¨å®¹å¨ä¸­å®æï¼æ éåºç¨åä¸ã
2. æäº¤`Runnable`å°çº¿ç¨æ± ãè¦æä¿®é¥°æä½`Runnable`ï¼æ è®ºæ¯ç´æ¥ä¿®é¥°`Runnable`è¿æ¯ä¿®é¥°çº¿ç¨æ± ï¼ã  
    è¿é¨åæä½ä¸å®æ¯å¨ç¨æ·åºç¨ä¸­è§¦åã
3. è¯»å`TTL`ï¼åä¸å¡æ£æ¥ã  
    å¨`SDK`ä¸­å®æï¼æ éåºç¨åä¸ã

åªæç¬¬2é¨åçæä½ååºç¨ä»£ç ç¸å³ã

å¦æä¸éè¿`Java Agent`ä¿®é¥°çº¿ç¨æ± ï¼åä¿®é¥°æä½éè¦åºç¨ä»£ç æ¥å®æã

ä½¿ç¨`Java Agent`æ¹å¼ï¼åºç¨æ éä¿®æ¹ä»£ç ï¼å³åå° ç¸å¯¹åºç¨ä»£ç  éæå°å®æè·¨çº¿ç¨æ± çä¸ä¸æä¼ éã

æ´å¤å³äºåºç¨åºæ¯çäºè§£è¯´æåè§ææ¡£[éæ±åºæ¯](requirement-scenario.md)ã

## å·²æ`Java Agent`ä¸­åµå¥`TTL Agent`

è¿æ ·å¯ä»¥åå°`Java`å¯å¨å½ä»¤è¡ä¸ç`Agent`çéç½®ã

å¨èªå·±ç`Agent`ä¸­å ä¸`TTL Agent`çé»è¾ï¼ç¤ºä¾ä»£ç å¦ä¸ï¼[`YourXxxAgent.java`](../ttl2-compatible/src/test/java/com/alibaba/demo/ttl/agent/YourXxxAgent.java)ï¼ï¼

```java
import com.alibaba.ttl.threadpool.agent.TtlAgent;
import com.alibaba.ttl.threadpool.agent.TtlTransformer;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.util.logging.Logger;

public final class YourXxxAgent {
    private static final Logger logger = Logger.getLogger(YourXxxAgent.class.getName());

    public static void premain(String agentArgs, Instrumentation inst) {
        TtlAgent.premain(agentArgs, inst); // add TTL Transformer

        // add your Transformer
        ...
    }
}
```

å³äº`Java Agent`å`ClassFileTransformer`çå¦ä½å®ç°å¯ä»¥åèï¼[`TtlAgent.java`](../ttl2-compatible/src/main/java/com/alibaba/ttl/threadpool/agent/TtlAgent.java)ã[`TtlTransformer.java`](../ttl2-compatible/src/main/java/com/alibaba/ttl/threadpool/agent/TtlTransformer.java)ã

æ³¨æï¼å¨`bootclasspath`ä¸ï¼è¿æ¯è¦å ä¸`TTL Jar`ï¼

```bash
-Xbootclasspath/a:/path/to/transmittable-thread-local-2.x.y.jar:/path/to/your/agent/jar/files
```

# ð¢ `Bootstrap ClassPath`ä¸æ·»å éç¨åº`Jar`çé®é¢åå¶è§£å³æ¹æ³

`TTL Agent`çä½¿ç¨æ¹å¼ï¼éè¦å°`TTL Jar`å å°`Bootstrap ClassPath`ä¸ï¼éè¿`Java`å½ä»¤è¡åæ°`-Xbootclasspath`ï¼ï¼è¿æ ·`TTL`çç±»ä¸`JDK`çæ ååºçç±»ï¼å¦`java.lang.String`ï¼ç`ClassLoader`æ¯ä¸æ ·çï¼é½å¨`Bootstrap ClassPath`ä¸ã

`Bootstrap ClassPath`ä¸çç±»ä¼ä¼åäºåºç¨`ClassPath`ç`Jar`è¢«å è½½ï¼å¹¶ä¸å è½½`ClassLoader`ä¸è½è¢«æ¹ã  
\# å½ç¶ææ¯ä¸ä¸¥æ ¼å°è¯´ï¼éè¿`Bootstrap ClassPath`ä¸çç±»ï¼å¦æ ååºçç±»ï¼æ¯å¯ä»¥æ¹`ClassLoader`çï¼ä½è¿æ ·åä¸è¬åªä¼å¸¦æ¥åç§éº»ç¦çé®é¢ãå³äº`ClassLoader`åå¶ä½¿ç¨æ³¨æçä»ç»è¯´æ å¯ä»¥åè§[ClassLoaderå§æå³ç³»çå®å¤éç½®](https://github.com/oldratlee/land#1-classloader%E5%A7%94%E6%89%98%E5%85%B3%E7%B3%BB%E7%9A%84%E5%AE%8C%E5%A4%87%E9%85%8D%E7%BD%AE)ã

`TTL Agent`èªå·±åé¨å®ç°ä½¿ç¨äº`Javassist`ï¼å³å¨`Bootstrap ClassPath`ä¸ä¹éè¦æ·»å `Javassist`ãå¦æåºç¨ä¸­ä¹ä½¿ç¨äº`Javassist`ï¼ç±äºè¿è¡æ¶ä¼ä¼åä½¿ç¨`TTL Agent`éç½®`Bootstrap ClassPath`ä¸ç`Javassist`ï¼åºç¨é»è¾è¿è¡æ¶å®éä¸è½éæ©/æå®åºç¨èªå·±ç`Javassist`ççæ¬ï¼å¸¦æ¥äº åºç¨éè¦ç`Javassist`ä¸`TTL Agent`ç¨ç`Javassist`ä¹é´çå¼å®¹æ§é£é©ã

å¯ä»¥éè¿ `repackage`ä¾èµï¼å³ éå½å/æ¹å ä¾èµç±»çååï¼æ¥è§£å³è¿ä¸ªé®é¢ã`Maven`æä¾äº[`Shade`æä»¶](https://maven.apache.org/plugins/maven-shade-plugin/)ï¼å¯ä»¥å®æä¸é¢çæä½ï¼

- `repackage` `Javassist`çç±»æä»¶
- æ·»å `repackage`è¿ç`Javassist`å°`TTL Jar`ä¸­

è¿æ ·æä½åï¼`TTL Agent`ä¸éè¦ä¾èµå¤é¨ç`Javassist`ä¾èµï¼ææä¸è¿æ ·ç`shade`è¿ç`TTL Jar`æ¯èªåå«çãå¨ä½¿ç¨ä¸æ¯ç¼è¯/è¿è¡æ¶0ä¾èµçï¼èªç¶ä¹è§é¿äºä¾èµå²çªçé®é¢ã

# ð¨ å¦ä½ç¼è¯æå»º

ç¼è¯æå»ºçç¯å¢è¦æ±ï¼ **_`JDK 8+`_**ï¼ç¨`Maven`å¸¸è§çæ¹å¼æ§è¡ç¼è¯æå»ºå³å¯ï¼  
\# å¨å·¥ç¨ä¸­å·²ç»åå«äºç¬¦åçæ¬è¦æ±ç`Maven`ï¼ç´æ¥è¿è¡ **_å·¥ç¨æ ¹ç®å½ä¸ç`mvnw`_**ï¼å¹¶ä¸éè¦åæå¨èªå·±å®è£å¥½`Maven`ã

```bash
# è¿è¡æµè¯Case
./mvnw test
# ç¼è¯æå
./mvnw package
# è¿è¡æµè¯Caseãç¼è¯æåãå®è£TTLåºå°Mavenæ¬å°
./mvnw install

#####################################################
# å¦æä½¿ç¨ä½ èªå·±å®è£ç mavenï¼çæ¬è¦æ±ï¼maven 3.3.9+
mvn install
```

# åå¸æä½åè¡¨

è¯¦è§ç¬ç«ææ¡£ [åå¸æä½åè¡¨](release-action-list.md)ã

# ð ç¸å³èµæ

## `JDK` core classes

- [WeakHashMap](https://docs.oracle.com/javase/10/docs/api/java/util/WeakHashMap.html)
- [InheritableThreadLocal](https://docs.oracle.com/javase/10/docs/api/java/lang/InheritableThreadLocal.html)

## `Java Agent`

- å®æ¹ææ¡£
    - [`Java Agent`è§è - `JavaDoc`](https://docs.oracle.com/javase/10/docs/api/java/lang/instrument/package-summary.html#package.description)
    - [JAR File Specification - JAR Manifest](https://docs.oracle.com/javase/10/docs/specs/jar/jar.html#jar-manifest)
    - [Working with Manifest Files - The Javaâ¢ Tutorials](https://docs.oracle.com/javase/tutorial/deployment/jar/manifestindex.html)
- [Java SE 6 æ°ç¹æ§: Instrumentation æ°åè½](https://www.ibm.com/developerworks/cn/java/j-lo-jse61/)
- [Creation, dynamic loading and instrumentation with javaagents](https://dhruba.name/2010/02/07/creation-dynamic-loading-and-instrumentation-with-javaagents/)
- [JavaAgentå è½½æºå¶åæ](https://www.iteye.com/blog/nijiaben-1847212/)

## `Javassist`

- [Getting Started with Javassist](https://www.javassist.org/tutorial/tutorial.html)

## `Maven Shade`æä»¶

- [`Maven Shade`æä»¶ææ¡£](https://maven.apache.org/plugins/maven-shade-plugin/)
