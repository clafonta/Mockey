package com.mockey.storage;

public class StorageRegistry {
	public static final IMockeyStorage MockeyStorage = InMemoryMockeyStorage.getInstance();
}
