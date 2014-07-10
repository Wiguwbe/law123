
void BigBallisticDemo::mouse(int button, int state, int x, int y)
{
    // Fire the current weapon.
    if (state == GLUT_DOWN) fire();
}

void BigBallisticDemo::key(unsigned char key)
{
    switch(key)
    {
    case '1': currentShotType = PISTOL; break;
    case '2': currentShotType = ARTILLERY; break;
    case '3': currentShotType = FIREBALL; break;
    case '4': currentShotType = LASER; break;

    case 'r': case 'R': reset(); break;
    }
}

/**
 * Called by the common demo framework to create an application
 * object (with new) and return a pointer.
 */
Application* getApplication()
{
    return new BigBallisticDemo();
}