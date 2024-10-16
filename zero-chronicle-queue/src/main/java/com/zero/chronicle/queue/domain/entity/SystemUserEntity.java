package com.zero.chronicle.queue.domain.entity;

import lombok.Data;
import net.openhft.chronicle.wire.Marshallable;

/**
 * 该类实现 `net.openhft.chronicle.wire.Marshallable` 并覆盖 `toString` 方法以实现更高效的序列化
 */
@Data
public class SystemUserEntity implements Marshallable {

    private String name;
    private int age;

    @Override
    public String toString() {
        return Marshallable.$toString(this);
    }

}
