

void PlatformDemo::key(unsigned char key)

/**
 * Called by the common demo framework to create an application
 * object (with new) and return a pointer.
 */
Application* getApplication()
{
    return new PlatformDemo();
}