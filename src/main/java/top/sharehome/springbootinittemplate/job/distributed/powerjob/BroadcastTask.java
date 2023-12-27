package top.sharehome.springbootinittemplate.job.distributed.powerjob;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.TaskResult;
import tech.powerjob.worker.core.processor.sdk.BroadcastProcessor;
import top.sharehome.springbootinittemplate.config.job.powerjob.condition.PowerJobCondition;

import java.util.List;

/**
 * 广播执行处理任务
 *
 * @author AntonyCheng
 */
@Slf4j
@Component
@Conditional(PowerJobCondition.class)
public class BroadcastTask implements BroadcastProcessor {

    @Override
    public ProcessResult preProcess(TaskContext context) {
        context.getOmsLogger().info("BroadcastTask#preProcess");
        return new ProcessResult(true, "success!");
    }

    @Override
    public ProcessResult process(TaskContext taskContext) throws Exception {
        taskContext.getOmsLogger().info("BroadcastTask#process");
        return new ProcessResult(true, "success!");
    }

    @Override
    public ProcessResult postProcess(TaskContext context, List<TaskResult> taskResults) {
        context.getOmsLogger().info("BroadcastTask#postProcess");
        return new ProcessResult(true, "success!");
    }
}
