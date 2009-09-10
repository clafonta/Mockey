#include <iostream>
#include <string>
#include <tchar.h>
#include <windows.h>
#include <winbase.h>
#include "include/jni.h"

using namespace std;

#define CLASS_NAME "com/mockey/runner/Main"
#define CLASS_PATH "-Djava.class.path=MockeyBooter.jar;jetty-runner.jar;Mockey.war;."

typedef jint (JNICALL *CreateJavaVM)(JavaVM **pvm, void **penv, void *args);
CreateJavaVM createJVM;

class Runner {
public:
	int Run() {
		JavaVM *vm;
		JavaVMInitArgs vm_args;
		JavaVMOption options[1];
		jint res;
		JNIEnv *env;
		jclass cls;
		jmethodID mid;

		options[0].optionString = CLASS_PATH;
		vm_args.version = JNI_VERSION_1_4;
		vm_args.options = options;
		vm_args.nOptions = 1;
		vm_args.ignoreUnrecognized = JNI_FALSE;

		HINSTANCE handle = GetJvmLibrary();
		if( handle == 0) {
			printf("Failed to load the jvm.");
			return -1;
		}

		createJVM = (CreateJavaVM)GetProcAddress(handle, "JNI_CreateJavaVM");

		res = createJVM(&vm, (void **)&env, &vm_args);
		if (res < 0)  {
			printf("Error creating JVM");
			return -1;
		}

		cls = env->FindClass(CLASS_NAME);
		if(cls == 0) {
			printf("Exception in thread \"main\" java.lang.NoClassDefFoundError: %s\n", CLASS_NAME);
			return -1;
		}

		mid = env->GetStaticMethodID(cls, "main", "([Ljava/lang/String;)V");
		if(mid == 0) {
			printf("Exception in thread \"main\" java.lang.NoSuchMethodError: main\n");
			return -1;
		}

		env->CallStaticVoidMethod(cls, mid, "");
		if(env->ExceptionCheck()) {
			env->ExceptionDescribe();
			return -1;
		}
		return 0;
	}

private:
	static HINSTANCE GetJvmLibrary()
	{
		char lszVersionValue[8192];
		char lszPathValue[8192];
		HKEY hKey;
		LONG returnStatus;
		DWORD dwType=REG_SZ;
		DWORD dwSize=8192;

		returnStatus = RegOpenKeyEx(
							HKEY_LOCAL_MACHINE, 
							"SOFTWARE\\JavaSoft\\Java Runtime Environment", 
							NULL,  
							KEY_ALL_ACCESS, 
							&hKey);
		
		if (returnStatus == ERROR_SUCCESS)
		{
			returnStatus = RegQueryValueEx(
								hKey, 
								TEXT("CurrentVersion"), 
								NULL, 
								&dwType, 
								(LPBYTE)&lszVersionValue, 
								&dwSize);

			if (returnStatus == ERROR_SUCCESS)
			{
				// error
			}
		}

		char buff[8192];
		memset(buff,0,sizeof(char)*8192);
		sprintf(buff, "SOFTWARE\\JavaSoft\\Java Runtime Environment\\%s", lszVersionValue);

		printf(buff);

		returnStatus = RegOpenKeyEx(
			HKEY_LOCAL_MACHINE, 
			buff, 
			NULL, 
			KEY_ALL_ACCESS, 
			&hKey);

		if (returnStatus == ERROR_SUCCESS)
		{
			printf("success in opening key.");

			returnStatus = RegQueryValueEx(
				hKey, 
				TEXT("RunTimeLib"), 
				NULL, 
				&dwType, 
				(LPBYTE)&lszPathValue, 
				&dwSize);

			while (returnStatus == ERROR_MORE_DATA)
			{
				returnStatus = RegQueryValueEx(
					hKey, 
					TEXT("RunTimeLib"), 
					NULL, 
					&dwType, 
					(LPBYTE)&lszPathValue, 
					&(dwSize+=4096));
			}

			if (returnStatus == ERROR_SUCCESS)
			{
				printf("success in reading path value.");
			} else {
				printf("failed to read value because of: %d",returnStatus);
			}
		}

		RegCloseKey(hKey);
			 
		printf("path value that was read: %s\n", lszPathValue);
			 
		HINSTANCE handle = LoadLibrary(lszPathValue);
		if( handle == 0) {
			printf("Failed to LoadLibrary.");
		} else {
			printf("succuss.");
		}
		return handle;
	}
};

int main()
{
	return Runner().Run();
}
