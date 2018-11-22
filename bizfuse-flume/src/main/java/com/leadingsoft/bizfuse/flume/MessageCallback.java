package com.leadingsoft.bizfuse.flume;

import java.util.List;

public interface MessageCallback<Message> {
    boolean process(List<Message> messages);
}
