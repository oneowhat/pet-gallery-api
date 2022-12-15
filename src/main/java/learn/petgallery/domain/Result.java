package learn.petgallery.domain;

import java.util.ArrayList;
import java.util.List;

public class Result<T> {
    private ResultType resultType = ResultType.SUCCESS;
    private final ArrayList<String> messages = new ArrayList<>();
    private T payload;

    public ResultType getResultType() {
        return resultType;
    }

    public boolean isSuccess() {
        return resultType == ResultType.SUCCESS;
    }

    public List<String> getMessages() {
        return new ArrayList<>(messages);
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    public void addMessage(String message, ResultType resultType) {
        messages.add(message);
        this.resultType = resultType;
    }
}
