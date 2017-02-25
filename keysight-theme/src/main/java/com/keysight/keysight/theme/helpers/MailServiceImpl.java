package com.keysight.keysight.theme.helpers;

import com.atlassian.core.task.MultiQueueTaskManager;
import com.atlassian.mail.queue.MailQueueItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;

/**
 * Default implementation of the {@link MailService}
 */
@ExportAsService
@Component
public class MailServiceImpl implements MailService
{
    public static final String MAIL = "mail";
    private final MultiQueueTaskManager taskManager;

    @Autowired
    public MailServiceImpl(MultiQueueTaskManager taskManager)
    {
        this.taskManager = taskManager;
    }

    /**
     * This will use a MultiQueueTaskManager to add add the mailQueueItem to a queue
     * to be sent
     *
     * @param mailQueueItem the item to send
     */
    @Override
    public void sendEmail(MailQueueItem mailQueueItem)
    {
        taskManager.addTask(MAIL, mailQueueItem);
    }
}
