package com.alibaba.ttl3.agent.transformlet.internal;

import com.alibaba.ttl3.agent.TtlAgent;
import com.alibaba.ttl3.agent.transformlet.TtlTransformlet;
import com.alibaba.ttl3.agent.transformlet.helper.AbstractExecutorTtlTransformlet;

import java.util.HashSet;
import java.util.Set;

/**
 * {@link TtlTransformlet} for {@link java.util.concurrent.ThreadPoolExecutor}
 * and {@link java.util.concurrent.ScheduledThreadPoolExecutor}.
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @author wuwen5 (wuwen.55 at aliyun dot com)
 * @see java.util.concurrent.ThreadPoolExecutor
 * @see java.util.concurrent.ScheduledThreadPoolExecutor
 */
public final class JdkExecutorTtlTransformlet extends AbstractExecutorTtlTransformlet implements TtlTransformlet {

    private static Set<String> getExecutorClassNames() {
        Set<String> executorClassNames = new HashSet<>();

        executorClassNames.add(THREAD_POOL_EXECUTOR_CLASS_NAME);
        executorClassNames.add("java.util.concurrent.ScheduledThreadPoolExecutor");

        return executorClassNames;
    }

    public JdkExecutorTtlTransformlet() {
        super(getExecutorClassNames(), TtlAgent.isDisableInheritableForThreadPool());
    }
}
