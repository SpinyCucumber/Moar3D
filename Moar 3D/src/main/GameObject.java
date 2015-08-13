package main;

import static org.lwjgl.glfw.Callbacks.errorCallbackPrint;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.io.IOException;
import java.nio.ByteBuffer;

import main.Field2.GenFunction;
import main.GLSLUtils.GLSLProgram;

import org.lwjgl.Sys;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWvidmode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
 
public class GameObject {
 
    // We need to strongly reference callback instances.
    private GLFWErrorCallback errorCallback;
    private GLFWKeyCallback   keyCallback;
    private Camera camera;
    private Framebuffer fbo;
    private GLSLProgram[] shaders = new GLSLProgram[2];
    
    private int width = 1000, height = 700, list;
    private double time, lastTime = getTime();
    
    private long window;
    
    public double getTime() {
    	return glfwGetTime();
    }
    
    public double delta() {
	    time = getTime();
	    try {
	    	return time - lastTime;
	    } finally {
    		lastTime = time;
    	}
    }
    
    public void run() {
    	
        System.out.println("Hello LWJGL " + Sys.getVersion() + "!");
        
        try {
        	
            init();
            loop();
 
            // Release window and window callbacks
            glfwDestroyWindow(window);
            keyCallback.release();
        } catch(Exception e) {
        	e.printStackTrace();
        } finally {
            for(GLSLProgram shader : shaders) shader.delete();
            glfwTerminate();
            errorCallback.release();
        }
        
    }
 
    private void init() throws IOException {
    	
        glfwSetErrorCallback(errorCallback = errorCallbackPrint(System.err));

        if ( glfwInit() != GL11.GL_TRUE ) throw new IllegalStateException("Unable to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);

        window = glfwCreateWindow(width, height, "", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");
 
        glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                    glfwSetWindowShouldClose(window, GL_TRUE);
            }
        });
        
        ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

        glfwSetWindowPos(
            window,
            (GLFWvidmode.width(vidmode) - width) / 2,
            (GLFWvidmode.height(vidmode) - height) / 2
        );
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        glfwSetCursorPos(window, 0, 0);
        
        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);

    }
 
    private void loop() throws IOException {
    	
        GLContext.createFromCurrent();
        glEnable(GL_TEXTURE_2D);
        glPointSize(20);

        shaders[0] = GLSLUtils.fromVertexFragmentPair("res/shader/lighting.vs", "res/shader/lighting.fs");
        shaders[1] = GLSLUtils.fromVertexFragmentPair("res/shader/fbo.vs", "res/shader/fbo.fs");
        shaders[0].use();
        shaders[0].setUniform("color", 1, 1, 1);
        
        fbo = new Framebuffer(width, height);
        camera = new Camera(window, new Vec3(0, 0, 0), 60, width / height, 0.01f, 100);
        Field2 field = new Field2(4, 4, new GenFunction(){
			public float gen(int x, int y) {
				return 0;
			}
        });
        field.scale(3);
        field.add(5);
        Model model = field.sphereModel();
        list = model.createList();
		
        while ( glfwWindowShouldClose(window) == GL_FALSE ) {
        	
        	delta();
        	glfwSetWindowTitle(window, "SWAG LEVEL: " + time);
        	
        	camera.applyPerspectiveMatrix();
            glEnable(GL_DEPTH_TEST);
        	camera.applyTransitions();
        	shaders[0].use();
        	shaders[0].setUniform("lightPosition", camera.position);
        	
        	fbo.bind();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            GL11.glCallList(list);
            Framebuffer.unbind();

            applyOrthogonalMatrix();
            glLoadIdentity();
            glDisable(GL_DEPTH_TEST);
            shaders[1].use();
            fbo.bindColorTexture();
            fullscreenQuad();
            
            glfwSwapBuffers(window);

            glfwPollEvents();
            camera.processMouse();
            camera.processKeyboard();
            
        }
        
    }
    
    private void fullscreenQuad() {
    	glBegin(GL_QUADS);
    	glTexCoord2f(0,0);
    	glVertex2f(0,0);
    	glTexCoord2f(1,0);
    	glVertex2f(width,0);
    	glTexCoord2f(1,1);
    	glVertex2f(width,height);
    	glTexCoord2f(0,1);
    	glVertex2f(0,height);
    	glEnd();
    }
    
    private void applyOrthogonalMatrix() {
    	glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, width, 0, height, -1, 1);
		glMatrixMode(GL_MODELVIEW);
    }
 
    public static void main(String[] args) {
        new GameObject().run();
    }
 
}
