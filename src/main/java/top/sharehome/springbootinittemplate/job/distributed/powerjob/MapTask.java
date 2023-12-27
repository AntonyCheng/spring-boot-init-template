package top.sharehome.springbootinittemplate.job.distributed.powerjob;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.MapProcessor;
import top.sharehome.springbootinittemplate.config.job.powerjob.condition.PowerJobCondition;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Map处理器 示例
 *
 * @author AntonyCheng
 */
@Slf4j
@Component
@Conditional(PowerJobCondition.class)
public class MapTask implements MapProcessor {

    /**
     * 每一批发送任务大小
     */
    private static final int BATCH_SIZE = 100;

    /**
     * 发送的批次
     */
    private static final int BATCH_NUM = 5;

    @Override
    public ProcessResult process(TaskContext context) throws Exception {

        context.getOmsLogger().info("MapTask#process");

        if (isRootTask()) {
            log.info("==== MAP ====");
            List<SubTask> subTasks = Lists.newLinkedList();
            for (int j = 0; j < BATCH_NUM; j++) {
                SubTask subTask = new SubTask();
                subTask.siteId = j;
                subTask.itemIds = Lists.newLinkedList();
                subTasks.add(subTask);
                for (int i = 0; i < BATCH_SIZE; i++) {
                    subTask.itemIds.add(i + j * 100);
                }
            }
            map(subTasks, "MAP_TEST_TASK");
            return new ProcessResult(true, "map successfully");
        } else {
            log.info("==== PROCESS ====");
            SubTask subTask = (SubTask) context.getSubTask();
            for (Integer itemId : subTask.getItemIds()) {
                if (Thread.interrupted()) {
                    // 任务被中断
                    log.info("job has been stop! so stop to process subTask: {} => {}", subTask.getSiteId(), itemId);
                    break;
                }
                log.info("processing subTask: {} => {}", subTask.getSiteId(), itemId);
                int max = Integer.MAX_VALUE >> 7;
                for (int i = 0; ; i++) {
                    // 模拟耗时操作
                    if (i > max) {
                        break;
                    }
                }
            }
            // 测试在 Map 任务中追加上下文
            context.getWorkflowContext().appendData2WfContext("Yasuo", "A sword's poor company for a long road.");
            boolean b = ThreadLocalRandom.current().nextBoolean();
            if (context.getCurrentRetryTimes() >= 1) {
                // 重试的话一定会成功
                b = true;
            }
            return new ProcessResult(b, "RESULT:" + b);
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubTask {
        private Integer siteId;
        private List<Integer> itemIds;
    }
}
