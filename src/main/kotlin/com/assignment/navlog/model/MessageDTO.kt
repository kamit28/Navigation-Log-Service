package com.assignment.navlog.model

class MessageDTO(messageType: MessageType, message: String) {
    enum class MessageType {
        SUCCESS, INFO, WARNING, ERROR
    }

    private var message: String? = message
    private var type: MessageType? = messageType
}