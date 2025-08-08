package top.sharehome.springbootinittemplate.config.ai.spring.service.chat.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * 深度思考流解析器
 *
 * @author AntonyCheng
 */
public class ReasonStreamParser {
    private final StringBuilder thinkContent = new StringBuilder();
    private final StringBuilder replyContent = new StringBuilder();

    // 添加跟踪上次获取的增量位置
    private int lastRetrievedThinkLength = 0;
    private int lastRetrievedReplyLength = 0;

    private boolean foundFirstThinkStart = false;
    private boolean thinkTagComplete = false;
    private int thinkTagDepth = 0;

    // 用于跟踪增量内容的长度
    private int lastThinkContentLength = 0;
    private int lastReplyContentLength = 0;

    // 标签匹配状态
    private enum TagState {
        // 普通文本
        NORMAL,
        // 正在匹配<think>
        MATCHING_START,
        // 正在匹配</think>
        MATCHING_END
    }

    private TagState currentState = TagState.NORMAL;
    // 用于临时存储可能的标签
    private final StringBuilder tagBuffer = new StringBuilder();

    // 标签模板
    private static final String START_TAG = "<think>";
    private static final String END_TAG = "</think>";

    /**
     * 处理流式输入的每个数据块
     * @param chunk 接收到的数据块
     */
    public void processChunk(String chunk) {
        for (char c : chunk.toCharArray()) {
            processChar(c);
        }
    }

    /**
     * 逐字符处理
     * @param c 当前字符
     */
    private void processChar(char c) {
        if (!thinkTagComplete) {
            processCharInThinkPhase(c);
        } else {
            // think阶段已完成，直接添加到回复内容
            replyContent.append(c);
            triggerReplyContentIncrement();
        }
    }

    /**
     * 在think阶段处理字符
     * @param c 当前字符
     */
    private void processCharInThinkPhase(char c) {
        switch (currentState) {
            case NORMAL:
                if (c == '<') {
                    tagBuffer.setLength(0);
                    tagBuffer.append(c);
                    currentState = TagState.MATCHING_START;
                } else {
                    // 如果还没找到think标签，直接当作回复内容处理
                    if (!foundFirstThinkStart) {
                        addToReplyContent(c);
                    } else {
                        addToCurrentContent(c);
                    }
                }
                break;
            case MATCHING_START:
                tagBuffer.append(c);
                if (tagBuffer.toString().equals(START_TAG)) {
                    // 匹配到完整的<think>标签
                    handleStartTag();
                    currentState = TagState.NORMAL;
                    tagBuffer.setLength(0);
                } else if ("</".contentEquals(tagBuffer)) {
                    // 可能是</think>的开始
                    currentState = TagState.MATCHING_END;
                } else if (!START_TAG.startsWith(tagBuffer.toString()) && !END_TAG.startsWith(tagBuffer.toString())) {
                    // 不匹配任何标签，输出缓存的内容
                    flushTagBufferBasedOnState();
                    currentState = TagState.NORMAL;
                }
                break;
            case MATCHING_END:
                tagBuffer.append(c);
                if (tagBuffer.toString().equals(END_TAG)) {
                    // 匹配到完整的</think>标签
                    handleEndTag();
                    currentState = TagState.NORMAL;
                    tagBuffer.setLength(0);
                } else if (!END_TAG.startsWith(tagBuffer.toString())) {
                    // 不匹配</think>标签，输出缓存的内容
                    flushTagBufferBasedOnState();
                    currentState = TagState.NORMAL;
                }
                break;
        }
    }

    /**
     * 处理<think>开始标签
     */
    private void handleStartTag() {
        if (!foundFirstThinkStart) {
            // 第一次遇到<think>标签
            foundFirstThinkStart = true;
            thinkTagDepth = 1;
        } else {
            // 嵌套的<think>标签，添加到当前内容
            thinkContent.append(START_TAG);
            thinkTagDepth++;
            // 触发think内容增量回调
            triggerThinkContentIncrement();
        }
    }

    /**
     * 处理</think>结束标签
     */
    private void handleEndTag() {
        if (foundFirstThinkStart) {
            thinkTagDepth--;
            if (thinkTagDepth == 0) {
                // 第一层think标签结束
                thinkTagComplete = true;
                onThinkContentComplete(thinkContent.toString());
            } else {
                // 嵌套标签的结束，添加到think内容
                thinkContent.append(END_TAG);
                triggerThinkContentIncrement();
            }
        } else {
            // 在找到第一个<think>之前遇到</think>，当作回复内容处理
            for (char c : END_TAG.toCharArray()) {
                addToReplyContent(c);
            }
        }
    }

    /**
     * 将字符添加到当前内容区域（think内容或回复内容）
     * @param c 字符
     */
    private void addToCurrentContent(char c) {
        if (foundFirstThinkStart && !thinkTagComplete) {
            thinkContent.append(c);
            triggerThinkContentIncrement();
        } else if (thinkTagComplete) {
            replyContent.append(c);
            triggerReplyContentIncrement();
        }
        // 如果还没找到第一个think标签，则忽略内容
    }

    /**
     * 触发think内容增量回调
     */
    private void triggerThinkContentIncrement() {
        int currentLength = thinkContent.length();
        if (currentLength > lastThinkContentLength) {
            String increment = thinkContent.substring(lastThinkContentLength, currentLength);
            lastThinkContentLength = currentLength;
            onThinkContentIncrement(increment);
        }
    }

    /**
     * 触发回复内容增量回调
     */
    private void triggerReplyContentIncrement() {
        int currentLength = replyContent.length();
        if (currentLength > lastReplyContentLength) {
            String increment = replyContent.substring(lastReplyContentLength, currentLength);
            lastReplyContentLength = currentLength;
            onReplyContentIncrement(increment);
            // 同时触发完整内容更新回调
            onReplyContentUpdate(replyContent.toString());
        }
    }

    /**
     * 根据当前状态将标签缓冲区内容输出到合适的区域
     */
    private void flushTagBufferBasedOnState() {
        String bufferedContent = tagBuffer.toString();
        for (char c : bufferedContent.toCharArray()) {
            if (!foundFirstThinkStart) {
                // 如果还没找到think标签，当作回复内容
                addToReplyContent(c);
            } else {
                // 否则按正常逻辑处理
                addToCurrentContent(c);
            }
        }
        tagBuffer.setLength(0);
    }

    /**
     * 直接添加到回复内容
     *
     * @param c 字符
     */
    private void addToReplyContent(char c) {
        replyContent.append(c);
        triggerReplyContentIncrement();
    }

    /**
     * think内容完成时的回调
     *
     * @param thinkContent 完整的思考内容
     */
    public void onThinkContentComplete(String thinkContent) {
    }

    /**
     * think内容增量回调（每次新增内容时触发）
     *
     * @param increment 新增的思考内容
     */
    public void onThinkContentIncrement(String increment) {
    }

    /**
     * 回复内容更新时的回调
     *
     * @param replyContent 当前的回复内容
     */
    public void onReplyContentUpdate(String replyContent) {
    }

    /**
     * 回复内容增量回调（每次新增内容时触发）
     *
     * @param increment 新增的回复内容
     */
    public void onReplyContentIncrement(String increment) {
    }

    /**
     * 获取当前的think内容
     *
     * @return think内容
     */
    public String getThinkContent() {
        String res = thinkContent.toString().trim();
        if (StringUtils.isNotBlank(res)){
            return res.trim();
        }
        return StringUtils.EMPTY;
    }

    /**
     * 获取think内容的增量部分
     *
     * @return 自上次调用此方法后新增的think内容
     */
    public String getThinkContentIncrement() {
        int currentLength = thinkContent.length();
        if (currentLength > lastRetrievedThinkLength) {
            String increment = thinkContent.substring(lastRetrievedThinkLength, currentLength);
            lastRetrievedThinkLength = currentLength;
            if (StringUtils.isNotBlank(increment)){
                return increment;
            }
            return StringUtils.EMPTY;
        }
        return StringUtils.EMPTY;
    }

    /**
     * 获取当前的回复内容
     *
     * @return 回复内容
     */
    public String getReplyContent() {
        String res = replyContent.toString();
        if (StringUtils.isNotBlank(res)){
            return res.trim();
        }
        return StringUtils.EMPTY;
    }

    /**
     * 获取reply内容的增量部分
     *
     * @return 自上次调用此方法后新增的reply内容
     */
    public String getReplyContentIncrement() {
        int currentLength = replyContent.length();
        if (currentLength > lastRetrievedReplyLength) {
            String increment = replyContent.substring(lastRetrievedReplyLength, currentLength);
            lastRetrievedReplyLength = currentLength;
            if (StringUtils.isNotBlank(increment)){
                return increment;
            }
            return StringUtils.EMPTY;
        }
        return StringUtils.EMPTY;
    }

    /**
     * 检查是否有think标签
     *
     * @return true如果检测到think标签
     */
    public boolean hasThinkContent() {
        return foundFirstThinkStart;
    }

    /**
     * 检查think内容是否已完成
     *
     * @return true如果think标签已完成解析
     */
    public boolean isThinkComplete() {
        return thinkTagComplete;
    }

    /**
     * 重置解析器状态
     */
    public void reset() {
        thinkContent.setLength(0);
        replyContent.setLength(0);
        lastRetrievedThinkLength = 0;
        lastRetrievedReplyLength = 0;
        foundFirstThinkStart = false;
        thinkTagComplete = false;
        thinkTagDepth = 0;
        currentState = TagState.NORMAL;
        tagBuffer.setLength(0);
    }

}