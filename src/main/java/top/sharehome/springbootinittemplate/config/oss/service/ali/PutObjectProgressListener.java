package top.sharehome.springbootinittemplate.config.oss.service.ali;

import com.aliyun.oss.event.ProgressEvent;
import com.aliyun.oss.event.ProgressEventType;
import com.aliyun.oss.event.ProgressListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 进度条监听器
 *
 * @author AntonyCheng
 */
@Slf4j
public class PutObjectProgressListener implements ProgressListener {

    private long bytesWritten = 0;

    @Getter
    private boolean succeed = false;

    @Override
    public void progressChanged(ProgressEvent progressEvent) {
        long bytes = progressEvent.getBytes();
        ProgressEventType eventType = progressEvent.getEventType();
        switch (eventType) {
            case TRANSFER_STARTED_EVENT:
                log.info("Start to upload......");
                break;
            case REQUEST_BYTE_TRANSFER_EVENT:
                this.bytesWritten += bytes;
                break;
            case TRANSFER_COMPLETED_EVENT:
                this.succeed = true;
                log.info("Succeed to upload, {} bytes have been transferred in total", this.bytesWritten);
                break;
            case TRANSFER_FAILED_EVENT:
                log.error("Failed to upload, {} bytes have been transferred", this.bytesWritten);
                break;
            default:
                break;
        }
    }

}