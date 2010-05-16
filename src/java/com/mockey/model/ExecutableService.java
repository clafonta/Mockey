package com.mockey.model;

public interface ExecutableService {
	public ResponseFromService execute(RequestFromClient request, Url realServiceUrl, String methodType);
}
