package app;

public interface Handler<T> {
    void handle(T t);
}
