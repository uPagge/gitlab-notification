package org.sadtech.bot.gitlab.core.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Адаптированная реализация Pair из пакета javafx.util. Реализация необходима, так как в некоторых сборках JDK этот
 * пакет может отсутствовать.
 *
 * @author mstruchkov 21.06.2019
 */
@Data
@AllArgsConstructor
public class Pair<K, V> {

    private K key;
    private V value;

}
