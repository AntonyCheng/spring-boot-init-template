package top.sharehome.springbootinittemplate.job.distributed.powerjob;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;
import tech.powerjob.worker.log.OmsLogger;
import top.sharehome.springbootinittemplate.config.job.powerjob.condition.PowerJobCondition;

/**
 * 单机执行处理任务
 *
 * @author AntonyCheng
 */
@Slf4j
@Component
@Conditional(PowerJobCondition.class)
public class BasicTask implements BasicProcessor {

    @Override
    public ProcessResult process(TaskContext context) {

        OmsLogger logger = context.getOmsLogger();

        logger.info("POWER-JOB, Hello World.");

        for (int i = 0; i < 5; i++) {
            logger.info("beat at:" + i);
        }

        return new ProcessResult(true, "success!");
    }

}