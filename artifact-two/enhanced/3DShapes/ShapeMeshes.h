/******************************************
 * ShapeMeshes
 * ---------------------------------------
 * This class provides methods for generating,
 * loading, and rendering various 3D primitive shapes.
 *
 * Supported shapes:
 * - Boxes, cones, cylinders, planes, prisms
 * - Pyramids, spheres, toruses, and more.
 *
 * Shapes are stored as GLMesh objects and rendered
 * using OpenGL VAO/VBO structures.
 ******************************************/

#pragma once

#include <GL/glew.h>
#include <glm/glm.hpp>
#include <vector>

#include <iostream>

 /******************************************
  * Vertex
  * ---------------------------------------
  * Represents a single vertex in 3D space,
  * containing position, normal, and texture
  * coordinate attributes.
  ******************************************/

struct Vertex {
    glm::vec3 position;
    glm::vec3 normal;
    glm::vec2 texCoord;  // Standardized name
};


/******************************************
 * GLMesh
 * ---------------------------------------
 * A structure encapsulating OpenGL buffers
 * for managing vertex data for a 3D shape.
 *
 * Members:
 * - vao: Vertex Array Object
 * - vbo: Vertex Buffer Object
 * - ebo: Element Buffer Object (for indexed drawing)
 * - nVertices: Number of vertices in the mesh
 * - nIndices: Number of indices for indexed drawing
 * - numSlices: Used for cylindrical and toroidal shapes
 ******************************************/

struct GLMesh {
    GLuint vao = 0;        // Vertex Array Object
    GLuint vbo = 0;        // Vertex Buffer Object
    GLuint vbos[2];     // Handles for the vertex buffer objects
    GLuint ebo = 0;        // Element Buffer Object (for indexed drawing)
    GLuint nVertices = 0;  // Number of vertices
    GLuint nIndices = 0;   // Number of indices
    int numSlices = 0;     // Number of slices (specific to cones, cylinders, etc.)

    //added for curved cone
    int curveSteps;
    GLuint ibo; // Index Buffer Object for glDrawElements

};

class ShapeMeshes
{
public:
    ShapeMeshes();  // Use default constructor

    /******************************************
     * InitializeMesh
     * ---------------------------------------
     * Creates an OpenGL VAO, VBO, and optionally an
     * EBO for a given GLMesh using vertex and index data.
     *
     * Params:
     * - mesh: Reference to the GLMesh structure.
     * - verts: Vector containing vertex data.
     * - indices: Vector containing index data
     *            (empty for non-indexed drawing).
     ******************************************/

    void InitializeMesh(GLMesh& mesh, const std::vector<GLfloat>& verts, const std::vector<GLuint>& indices);

    /******************************************
     * BoxSide Enum
     * ---------------------------------------
     * Defines the six sides of a box for selective
     * rendering using `DrawBoxMeshSide()`.
     ******************************************/

    enum class BoxSide {
        back,
        bottom,
        left,
        right,
        top,
        front
    };

    /******************************************
     * LoadXMesh (Various)
     * ---------------------------------------
     * Generates and loads different 3D primitive
     * meshes into OpenGL buffers.
     *
     * Each function constructs the vertex and index
     * data for the respective shape and stores them
     * in a GLMesh.
     *
     * Supported shapes:
     * - LoadBoxMesh()
     * - LoadConeMesh(radius, height, numSlices)
     * - LoadCylinderMesh(radius, height, numSlices)
     * - LoadPlaneMesh(width, height)
     * - LoadPrismMesh()
     * - LoadPyramid3Mesh()
     * - LoadPyramid4Mesh(baseSize, height)
     * - LoadSphereMesh(latSegments, lonSegments, radius)
     * - LoadTaperedCylinderMesh(bottomRadius, topRadius, height, numSlices)
     * - LoadTorusMesh(mainRadius, tubeRadius, mainSegments, tubeSegments)
     ******************************************/

    void LoadBoxMesh();
    void LoadConeMesh(float radius = 1.0f, float height = 1.0f, int numSlices = 18);
    void LoadCylinderMesh(float radius = 1.0f, float height = 1.0f, int numSlices = 36);
    void LoadPlaneMesh(float width = 2.0f, float height = 2.0f);
    void LoadPrismMesh();
    void LoadPyramid3Mesh();
    void LoadPyramid4Mesh(float baseSize = 1.0f, float height = 1.0f);
    void LoadSphereMesh(int latitudeSegments = 18, int longitudeSegments = 18, float radius = 1.0f);
    void LoadHemisphereMesh(int latitudeSegments = 18, int longitudeSegments = 18, float radius = 1.0f);

    void LoadTaperedCylinderMesh(float bottomRadius = 1.0f, float topRadius = 0.5f, float height = 1.0f, int numSlices = 18);
    void LoadTorusMesh(float mainRadius = 1.0f, float tubeRadius = 0.25f, int mainSegments = 18, int tubeSegments = 18);
    // the following torus meshes are provided in case multiple tori of different thicknesses are needed
    void LoadExtraTorusMesh1(float thickness = 0.4);
    void LoadExtraTorusMesh2(float thickness = 0.6);
    void LoadSpringMesh(float mainRadius = 1.0f, float tubeRadius = 0.1f, int mainSegments = 6, int tubeSegments = 18, float springLength = 4.0f);
    void LoadTubeMesh(float outerRadius = 2.0f, float innerRadius1 = 1.7f, float height = 1.0f, int numSlices = 30);
    void LoadFinMesh(float baseLength = 2.9f, float topLength = 0.75f, float height = 2.5f, float thickness = 0.1f);

    /******************************************
     * DrawXMesh (Various)
     * ---------------------------------------
     * Renders the respective 3D shape using OpenGL.
     *
     * Uses glDrawElements (for indexed drawing)
     * or glDrawArrays (for non-indexed meshes).
     *
     * Supported functions:
     * - DrawBoxMesh(wireframe)
     * - DrawBoxMeshSide(side, wireframe)
     * - DrawConeMesh(bDrawBottom, wireframe)
     * - DrawCylinderMesh(bDrawTop, bDrawBottom, bDrawSides, wireframe)
     * - DrawPlaneMesh(wireframe)
     * - DrawPrismMesh(wireframe)
     * - DrawPyramid3Mesh(wireframe)
     * - DrawPyramid4Mesh(wireframe)
     * - DrawSphereMesh(wireframe)
     * - DrawHalfSphereMesh(wireframe)
     * - DrawTaperedCylinderMesh(bDrawTop, bDrawBottom, bDrawSides, wireframe)
     * - DrawTorusMesh(wireframe)
     * - DrawHalfTorusMesh(wireframe)
     ******************************************/

    void DrawBoxMesh(bool wireframe = false) const;
    void DrawBoxMeshSide(BoxSide side, bool wireframe = false);
    void DrawConeMesh(bool bDrawBottom = true, bool wireframe = false);
    void DrawPartialConeMesh(float radius, float height, int numSlices, float arcDegrees, bool wireframe);
    void DrawCylinderMesh(bool bDrawTop = true, bool bDrawBottom = true, bool bDrawSides = true, bool wireframe = false);
    void DrawPlaneMesh(bool wireframe = false);
    void DrawPrismMesh(bool wireframe = false);
    void DrawPyramid3Mesh(bool wireframe = false);
    void DrawPyramid4Mesh(bool wireframe = false);
    void DrawSphereMesh(bool wireframe = false);
    void DrawHemisphereMesh(bool wireframe = false);

    void DrawFinMesh(bool wireframe);
    void DrawFinSides();
    void DrawFinFrontOnly();
    void DrawFinBackOnly();
    void DrawFinUntexturedSides();
    void DrawTaperedCylinderMesh(bool bDrawTop = true, bool bDrawBottom = true, bool bDrawSides = true, bool wireframe = false);
    void DrawTorusMesh(bool wireframe = false);
    // the following torus meshes are provided in case multiple tori of different thicknesses are needed
    void DrawExtraTorusMesh1();
    void DrawExtraTorusMesh2();

    void DrawHalfTorusMesh(bool wireframe = false);
    void DrawSpringMesh(bool wireframe = false);
    void DrawTubeMesh(bool wireframe = false) const;

    /******************************************
    * Deprecated Functions
    * ****************************************/
    void DrawHalfSphereMesh(bool wireframe = false);

    void DrawBoxMeshLines();
    void DrawConeMeshLines();
    void DrawCylinderMeshLines();
    void DrawPlaneMeshLines();
    void DrawPrismMeshLines();
    void DrawPyramid3MeshLines();
    void DrawPyramid4MeshLines();
    void DrawSphereMeshLines();
    void DrawHalfSphereMeshLines();
    void DrawTaperedCylinderMeshLines();
    void DrawTorusMeshLines();
    void DrawHalfTorusMeshLines();
    //void DrawHalfSphereMesh();

    //added custom shapes
    void LoadCurvedConeMesh(int numSlices, int curveSteps, float radius, float height, float curveFactor);
    void DrawCurvedConeMesh();
    void LoadTaperedTorusMesh();
    void DrawTaperedTorusMesh(float mainRadius, float tubeRadiusStart, float tubeRadiusEnd, int mainSegments, int tubeSegments, float sweepAngleRadians);
    void LoadSpiralMesh();
    void DrawSpiralMesh(float tubeRadius, float flattenFactor, float loopSpacing, float numLoops, int tubeSegments, int spiralSegments);
    void LoadSineConeMesh();
    void DrawSineConeMesh(float baseRadius, float height, float flattenFactor, float sineAmplitude, float sineFrequency, float sinePhase, int radialSegments, int heightSegments);
    // NEW: Superellipsoid
    void LoadSuperellipsoidMesh();
    void DrawSuperellipsoidMesh(float scaleX,
        float scaleY,
        float scaleZ,
        float verticalExponent,
        float horizontalExponent,
        int   uSegments,
        int   vSegments);


private:
    // Flags to track whether warnings have already been shown
    bool boxWarned = false;
    bool coneWarned = false;
    bool cylinderWarned = false;
    bool planeWarned = false;
    bool prismWarned = false;
    bool pyramid3Warned = false;
    bool pyramid4Warned = false;
    bool sphereWarned = false;
    bool halfSphereWarned = false;
    bool taperedCylinderWarned = false;
    bool torusWarned = false;
    bool halfTorusWarned = false;

    bool m_bMemoryLayoutDone = false;   // Ensures memory layout is only set once

    // Mesh storage for different shapes
    GLMesh m_BoxMesh;
    GLMesh m_ConeMesh;
    GLMesh m_CylinderMesh;
    GLMesh m_PlaneMesh;
    GLMesh m_PrismMesh;
    GLMesh m_Pyramid3Mesh;
    GLMesh m_Pyramid4Mesh;
    GLMesh m_SphereMesh;
    GLMesh m_HemisphereMesh;
    GLMesh m_TaperedCylinderMesh;
    GLMesh m_TorusMesh;
    // the following torus meshes are provided in case multiple tori of different thicknesses are needed
    GLMesh m_ExtraTorusMesh1;
    GLMesh m_ExtraTorusMesh2;
    GLMesh m_SpringMesh; // Add this line to declare m_SpringMesh
    GLMesh m_TubeMesh;
    GLMesh m_FinMesh;

    // custom shapes
    GLMesh m_CurvedConeMesh;
    GLMesh m_TaperedTorusMesh;
    GLMesh m_SpiralMesh;
    GLMesh m_SineConeMesh;
    GLMesh m_SuperellipsoidMesh; // NEW


    bool m_IsMemoryLayoutSet = false;  // Improved variable naming

    /******************************************
     * Normal Calculation Functions
     * ---------------------------------------
     * Computes surface normals for quads and triangles.
     *
     * - CalculateTriangleNormal(p1, p2, p3): Computes
     *   the normal for a triangle using the cross product.
     * - QuadCrossProduct(p1, p2, p3, p4): Computes
     *   an averaged normal for a quad.
     ******************************************/

    glm::vec3 CalculateTriangleNormal(const glm::vec3& p1, const glm::vec3& p2, const glm::vec3& p3);
    glm::vec3 QuadCrossProduct(const glm::vec3& p1, const glm::vec3& p2, const glm::vec3& p3, const glm::vec3& p4);

    /******************************************
     * SetShaderMemoryLayout
     * ---------------------------------------
     * Configures vertex attribute pointers for
     * the shader program.
     *
     * - Position: Layout location 0 (vec3)
     * - Normal: Layout location 1 (vec3)
     * - Texture Coordinates: Layout location 2 (vec2)
     ******************************************/

    void SetShaderMemoryLayout();
};
