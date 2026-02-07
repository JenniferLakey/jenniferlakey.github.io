///////////////////////////////////////////////////////////////////////////////
// ShapeMeshes.cpp
// ===============
// Contains mesh generation functions for various 3D primitives:
//   - Box, Cone, Cylinder, Plane, Prism, Sphere, Tapered Cylinder, Torus, Pyramid
//
// These meshes are stored in VAO/VBO structures and prepared for OpenGL rendering.
//
//  AUTHOR: Phil Enkema - SNHU Instructor / Computer Science
//  Created for CS-330 Computational Graphics and Visualization - Mar 1, 2025
///////////////////////////////////////////////////////////////////////////////

#include "shapemeshes.h"

// GLM Math Header inclusions
#include <glm/glm.hpp>
#include <glm/gtc/type_ptr.hpp>

#include <array> // Required for std::array
#include <vector> // Required for std::vector
#include <cmath>  // Required for math functions like sqrt and cos

#include <iostream>

namespace Constants
{
	constexpr float Pi = 3.141592653589793;       // Use constexpr for compile-time evaluation
	constexpr float PiHalf = Pi / 2.0;           // Use computed value for better accuracy
	constexpr GLuint FloatsPerVertex = 3;         // Number of coordinates per vertex
	constexpr GLuint FloatsPerNormal = 3;         // Number of components per normal vector
	constexpr GLuint FloatsPerUV = 2;             // Number of texture coordinate values
}

using namespace Constants;

/******************************************
 * ShapeMeshes Constructor
 * ---------------------------------------
 * Default constructor for ShapeMeshes.
 * Ensures proper initialization.
 ******************************************/

 //ShapeMeshes::ShapeMeshes() = default;  // Define in the .cpp file

 // Constructor initializes warning flags to false
ShapeMeshes::ShapeMeshes()
	: boxWarned(false), coneWarned(false), cylinderWarned(false), planeWarned(false),
	prismWarned(false), pyramid3Warned(false), pyramid4Warned(false), sphereWarned(false),
	halfSphereWarned(false), taperedCylinderWarned(false), torusWarned(false), halfTorusWarned(false) {
}

/******************************************
 * SetWireframeMode
 * ---------------------------------------
 * Configures OpenGL to render meshes in either
 * wireframe mode or solid mode.
 *
 * @param wireframe If true, sets to wireframe mode.
 ******************************************/

inline void SetWireframeMode(bool wireframe) {
	glPolygonMode(GL_FRONT_AND_BACK, wireframe ? GL_LINE : GL_FILL);
}

/******************************************
 * InitializeMesh
 * ---------------------------------------
 * Generic function to initialize a mesh by
 * generating and binding VAOs/VBOs/EBOs.
 *
 * @param mesh Reference to the GLMesh struct.
 * @param verts Vector containing vertex data.
 * @param indices Vector containing index data (if applicable).
 ******************************************/

void ShapeMeshes::InitializeMesh(GLMesh& mesh, const std::vector<GLfloat>& verts, const std::vector<GLuint>& indices) {
	mesh.nVertices = static_cast<GLuint>(verts.size() / (FloatsPerVertex + FloatsPerNormal + FloatsPerUV));
	mesh.nIndices = static_cast<GLuint>(indices.size());

	glGenVertexArrays(1, &mesh.vao);
	glBindVertexArray(mesh.vao);

	glGenBuffers(1, &mesh.vbo);
	glBindBuffer(GL_ARRAY_BUFFER, mesh.vbo);
	glBufferData(GL_ARRAY_BUFFER, verts.size() * sizeof(GLfloat), verts.data(), GL_STATIC_DRAW);

	if (!indices.empty()) {
		glGenBuffers(1, &mesh.ebo);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mesh.ebo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices.size() * sizeof(GLuint), indices.data(), GL_STATIC_DRAW);
	}

	if (!m_bMemoryLayoutDone) {
		SetShaderMemoryLayout();
	}

	glBindVertexArray(0); // Unbind VAO after setup
}

/******************************************
 * LoadBoxMesh
 * ---------------------------------------
 * Generates a 3D box mesh with vertex positions,
 * normals, and texture coordinates.
 *
 * - Vertices are defined in a standard cube format.
 * - Indexed drawing is used to optimize rendering.
 *
 * Correct draw call:
 * glDrawElements(GL_TRIANGLES, meshes.gBoxMesh.nIndices, GL_UNSIGNED_INT, (void*)0);
 ******************************************/

void ShapeMeshes::LoadBoxMesh()
{
	// Box vertex data (Positions, Normals, Texture Coords)
	const std::vector<GLfloat> verts = {
		// Positions         // Normals         // Texture Coords
		// Back Face
		 0.5f,  0.5f, -0.5f,   0.0f,  0.0f, -1.0f,   0.0f, 1.0f,  // 0
		 0.5f, -0.5f, -0.5f,   0.0f,  0.0f, -1.0f,   0.0f, 0.0f,  // 1
		-0.5f, -0.5f, -0.5f,   0.0f,  0.0f, -1.0f,   1.0f, 0.0f,  // 2
		-0.5f,  0.5f, -0.5f,   0.0f,  0.0f, -1.0f,   1.0f, 1.0f,  // 3

		// Bottom Face
		-0.5f, -0.5f,  0.5f,   0.0f, -1.0f,  0.0f,   0.0f, 1.0f,  // 4
		-0.5f, -0.5f, -0.5f,   0.0f, -1.0f,  0.0f,   0.0f, 0.0f,  // 5
		 0.5f, -0.5f, -0.5f,   0.0f, -1.0f,  0.0f,   1.0f, 0.0f,  // 6
		 0.5f, -0.5f,  0.5f,   0.0f, -1.0f,  0.0f,   1.0f, 1.0f,  // 7

		 // Left Face
		 -0.5f,  0.5f, -0.5f,  -1.0f,  0.0f,  0.0f,   0.0f, 1.0f,  // 8
		 -0.5f, -0.5f, -0.5f,  -1.0f,  0.0f,  0.0f,   0.0f, 0.0f,  // 9
		 -0.5f, -0.5f,  0.5f,  -1.0f,  0.0f,  0.0f,   1.0f, 0.0f,  // 10
		 -0.5f,  0.5f,  0.5f,  -1.0f,  0.0f,  0.0f,   1.0f, 1.0f,  // 11

		 // Right Face
		  0.5f,  0.5f,  0.5f,   1.0f,  0.0f,  0.0f,   0.0f, 1.0f,  // 12
		  0.5f, -0.5f,  0.5f,   1.0f,  0.0f,  0.0f,   0.0f, 0.0f,  // 13
		  0.5f, -0.5f, -0.5f,   1.0f,  0.0f,  0.0f,   1.0f, 0.0f,  // 14
		  0.5f,  0.5f, -0.5f,   1.0f,  0.0f,  0.0f,   1.0f, 1.0f,  // 15

		  // Top Face
		  -0.5f,  0.5f, -0.5f,   0.0f,  1.0f,  0.0f,   0.0f, 1.0f,  // 16
		  -0.5f,  0.5f,  0.5f,   0.0f,  1.0f,  0.0f,   0.0f, 0.0f,  // 17
		   0.5f,  0.5f,  0.5f,   0.0f,  1.0f,  0.0f,   1.0f, 0.0f,  // 18
		   0.5f,  0.5f, -0.5f,   0.0f,  1.0f,  0.0f,   1.0f, 1.0f,  // 19

		   // Front Face
		   -0.5f,  0.5f,  0.5f,   0.0f,  0.0f,  1.0f,   0.0f, 1.0f,  // 20
		   -0.5f, -0.5f,  0.5f,   0.0f,  0.0f,  1.0f,   0.0f, 0.0f,  // 21
			0.5f, -0.5f,  0.5f,   0.0f,  0.0f,  1.0f,   1.0f, 0.0f,  // 22
			0.5f,  0.5f,  0.5f,   0.0f,  0.0f,  1.0f,   1.0f, 1.0f   // 23
	};

	// Corrected Index Data
	const std::vector<GLuint> indices = {
		0, 1, 2, 2, 3, 0,   // Back
		4, 5, 6, 6, 7, 4,   // Bottom
		8, 9, 10, 10, 11, 8, // Left
		12, 13, 14, 14, 15, 12, // Right
		16, 17, 18, 18, 19, 16, // Top
		20, 21, 22, 22, 23, 20  // Front
	};

	// Store vertex and index counts
	m_BoxMesh.nVertices = verts.size() / (FloatsPerVertex + FloatsPerNormal + FloatsPerUV);
	m_BoxMesh.nIndices = indices.size();

	// Use the centralized InitializeMesh function
	InitializeMesh(m_BoxMesh, verts, indices);
}


/******************************************
 * LoadConeMesh
 * ---------------------------------------
 * Generates a cone mesh using a triangle fan
 * for the base and a triangle strip for the sides.
 *
 * @param radius Base radius of the cone.
 * @param height Height of the cone.
 * @param numSlices Number of subdivisions around the base.
 *
 * Correct draw calls:
 * glDrawArrays(GL_TRIANGLE_FAN, 0, numSlices + 2);     // Base
 * glDrawArrays(GL_TRIANGLE_STRIP, numSlices + 2, numSlices * 2); // Sides
 ******************************************/

void ShapeMeshes::LoadConeMesh(float radius, float height, int numSlices)
{
	if (numSlices < 3) numSlices = 3;
	m_ConeMesh.numSlices = numSlices;

	std::vector<GLfloat> vertices;
	std::vector<GLuint>  indices;

	float angleStep = 2.0f * Pi / numSlices;

	// --- Bottom cap (fan) ---
	int bottomCenterIndex = vertices.size() / 8;
	// center
	vertices.insert(vertices.end(), {
		0,0,0,     0,-1,0,      0.5f,0.5f
		});
	// rim (no duplicate at end)
	for (int i = 0; i < numSlices; ++i) {
		float a = i * angleStep;
		float x = radius * cos(a), z = radius * sin(a);
		vertices.insert(vertices.end(), {
			x,0,z,  0,-1,0,   0.5f + 0.5f * cos(a), 0.5f + 0.5f * sin(a)
			});
		// fan triangles, CCW order as seen from below
		indices.push_back(bottomCenterIndex);
		indices.push_back(bottomCenterIndex + ((i + 1) % numSlices) + 1);
		indices.push_back(bottomCenterIndex + i + 1);
	}

	// --- Apex ---
	int apexIndex = vertices.size() / 8;
	vertices.insert(vertices.end(), {
		0,height,0,   0,1,0,    0.5f,0.5f
		});

	// --- Side ring ---
	int sideStart = apexIndex + 1;
	for (int i = 0; i < numSlices; ++i) {
		float a0 = i * angleStep;
		float a1 = (i + 1) * angleStep;
		glm::vec3 p0(radius * cos(a0), 0, radius * sin(a0));
		glm::vec3 p1(radius * cos(a1), 0, radius * sin(a1));
		// same normal for the whole quad (averaged)
		glm::vec3 normal = glm::normalize(glm::vec3(
			(p0.x + p1.x) * 0.5f,
			height * 0.5f,
			(p0.z + p1.z) * 0.5f
		));
		// push two verts per slice
		vertices.insert(vertices.end(), {
			p0.x,p0.y,p0.z, normal.x,normal.y,normal.z, (float)i / numSlices, 1.0f
			});
		vertices.insert(vertices.end(), {
			p1.x,p1.y,p1.z, normal.x,normal.y,normal.z, (float)(i + 1) / numSlices, 1.0f
			});

		// now two verts at sideStart + 2*i, 2*i+1
		int v0 = sideStart + 2 * i;
		int v1 = sideStart + 2 * i + 1;
		// CCW winding looking at outside of cone
		indices.push_back(apexIndex);
		indices.push_back(v0);
		indices.push_back(v1);
	}

	// --- Upload mesh ---
	InitializeMesh(m_ConeMesh, vertices, indices);
}


/******************************************
 * LoadCylinderMesh
 * ---------------------------------------
 * Generates a cylinder mesh consisting of:
 *  - A circular base using a triangle fan.
 *  - A circular top using a triangle fan.
 *  - The sides using a triangle strip.
 *
 * @param radius Radius of the cylinder.
 * @param height Height of the cylinder.
 * @param numSlices Number of subdivisions around the circular base.
 *
 * Correct draw calls:
 * glDrawArrays(GL_TRIANGLE_FAN, 0, numSlices + 2);   // Bottom cap
 * glDrawArrays(GL_TRIANGLE_FAN, numSlices + 2, numSlices + 2); // Top cap
 * glDrawArrays(GL_TRIANGLE_STRIP, 2 * (numSlices + 2), numSlices * 2); // Sides
 ******************************************/

void ShapeMeshes::LoadCylinderMesh(float radius, float height, int numSlices)
{
	if (numSlices < 3) numSlices = 3;
	m_CylinderMesh.numSlices = numSlices;

	std::vector<GLfloat> vertices;
	std::vector<GLuint> indices;

	float angleStep = 2.0f * Pi / numSlices;

	// **Generate Bottom Cap**
	int bottomCenterIndex = static_cast<int>(vertices.size() / 8);
	vertices.insert(vertices.end(), { 0.0f, 0.0f, 0.0f,  0.0f, -1.0f, 0.0f,  0.5f, 0.5f });

	for (int i = 0; i <= numSlices; ++i) {
		float angle = i * angleStep;
		float x = radius * cos(angle);
		float z = radius * sin(angle);
		vertices.insert(vertices.end(), { x, 0.0f, z,  0.0f, -1.0f, 0.0f,  0.5f + 0.5f * cos(angle), 0.5f + 0.5f * sin(angle) });

		if (i < numSlices)
		{
			indices.push_back(bottomCenterIndex);
			indices.push_back(static_cast<GLuint>(bottomCenterIndex + i + 1));
			indices.push_back(static_cast<GLuint>(bottomCenterIndex + (i + 1) % numSlices + 1));
		}
	}

	// **Generate Top Cap**
	int topCenterIndex = static_cast<int>(vertices.size() / 8);
	vertices.insert(vertices.end(), { 0.0f, height, 0.0f,  0.0f, 1.0f, 0.0f,  0.5f, 0.5f });

	for (int i = 0; i <= numSlices; ++i) {
		float angle = i * angleStep;
		float x = radius * cos(angle);
		float z = radius * sin(angle);
		vertices.insert(vertices.end(), { x, height, z,  0.0f, 1.0f, 0.0f,  0.5f + 0.5f * cos(angle), 0.5f + 0.5f * sin(angle) });

		if (i < numSlices)
		{
			indices.push_back(topCenterIndex);
			indices.push_back(static_cast<GLuint>(topCenterIndex + i + 1));
			indices.push_back(static_cast<GLuint>(topCenterIndex + (i + 1) % numSlices + 1));
		}
	}

	// **Generate Side Faces**
	int sideStartIndex = static_cast<int>(vertices.size() / 8);
	for (int i = 0; i <= numSlices; ++i) {
		float angle = i * angleStep;
		float x = radius * cos(angle);
		float z = radius * sin(angle);
		float nx = cos(angle);
		float nz = sin(angle);

		// Bottom ring vertex
		vertices.insert(vertices.end(), { x, 0.0f, z,  nx, 0.0f, nz,  static_cast<float>(i) / numSlices, 1.0f });
		// Top ring vertex
		vertices.insert(vertices.end(), { x, height, z,  nx, 0.0f, nz,  static_cast<float>(i) / numSlices, 0.0f });

		if (i < numSlices)
		{
			indices.push_back(sideStartIndex + (i * 2));
			indices.push_back(sideStartIndex + (i * 2) + 1);
			indices.push_back(sideStartIndex + ((i + 1) * 2));

			indices.push_back(sideStartIndex + (i * 2) + 1);
			indices.push_back(sideStartIndex + ((i + 1) * 2));
			indices.push_back(sideStartIndex + ((i + 1) * 2) + 1);
		}
	}

	// **Initialize Mesh**
	InitializeMesh(m_CylinderMesh, vertices, indices);
}

/******************************************
 * LoadPlaneMesh
 * ---------------------------------------
 * Creates a flat plane mesh positioned at the origin.
 * The plane is centered and aligned with the XZ plane.
 *
 * @param width Width of the plane.
 * @param height Height of the plane.
 *
 * Correct draw call:
 * glDrawElements(GL_TRIANGLES, meshes.gPlaneMesh.nIndices, GL_UNSIGNED_INT, (void*)0);
 ******************************************/

void ShapeMeshes::LoadPlaneMesh(float width, float height) {
	// Half dimensions for centering the plane
	float halfWidth = width / 2.0f;
	float halfHeight = height / 2.0f;

	// Vertex data: Positions, Normals, Texture Coords
	std::vector<GLfloat> verts = {
		// Vertex Positions       // Normals           // Texture Coords
		-halfWidth, 0.0f, halfHeight,   0.0f, 1.0f, 0.0f,   0.0f, 0.0f,  // Bottom-left
		 halfWidth, 0.0f, halfHeight,   0.0f, 1.0f, 0.0f,   1.0f, 0.0f,  // Bottom-right
		 halfWidth, 0.0f, -halfHeight,  0.0f, 1.0f, 0.0f,   1.0f, 1.0f,  // Top-right
		-halfWidth, 0.0f, -halfHeight,  0.0f, 1.0f, 0.0f,   0.0f, 1.0f   // Top-left
	};

	// Index data
	std::vector<GLuint> indices = {
		0, 1, 2,  // First triangle
		0, 2, 3   // Second triangle
	};

	// Store vertex and index count
	m_PlaneMesh.nVertices = verts.size() / (FloatsPerVertex + FloatsPerNormal + FloatsPerUV);
	m_PlaneMesh.nIndices = static_cast<GLuint>(indices.size());

	// Use the centralized InitializeMesh function
	InitializeMesh(m_PlaneMesh, verts, indices);
}

/******************************************
 * LoadPrismMesh
 * ---------------------------------------
 * Generates a 3D triangular prism mesh with
 * predefined vertices and normals.
 *
 * - The base consists of a triangle.
 * - The prism has 5 faces: 2 triangular and 3 rectangular.
 * - Uses indexed drawing for efficiency.
 *
 * @note The function initializes the mesh using `InitializeMesh()`.
 *
 * Correct draw call:
 * glDrawArrays(GL_TRIANGLES, 0, prismMesh.nVertices);
 ******************************************/

void ShapeMeshes::LoadPrismMesh()
{
	// Vertex data
	GLfloat verts[] = {
		//Positions				//Normals
		// ------------------------------------------------------

		//Back Face				//Negative Z Normal  
		0.5f, 0.5f, -0.5f,		0.0f,  0.0f, -1.0f,		0.0f, 1.0f,
		0.5f, -0.5f, -0.5f,		0.0f,  0.0f, -1.0f,		0.0f, 0.0f,
		-0.5f, -0.5f, -0.5f,	0.0f,  0.0f, -1.0f,		1.0f, 0.0f,
		0.5f, 0.5f, -0.5f,		0.0f,  0.0f, -1.0f,		0.0f, 1.0f,
		0.5f,  0.5f, -0.5f,		0.0f,  0.0f, -1.0f,		0.0f, 1.0f,
		-0.5f,  0.5f, -0.5f,	0.0f,  0.0f, -1.0f,		1.0f, 1.0f,
		-0.5f, -0.5f, -0.5f,	0.0f,  0.0f, -1.0f,		1.0f, 0.0f,
		0.5f,  0.5f, -0.5f,		0.0f,  0.0f, -1.0f,		0.0f, 1.0f,

		//Bottom Face			//Negative Y Normal
		0.5f, -0.5f, -0.5f,		0.0f, -1.0f,  0.0f,		0.0f, 0.0f,
		-0.5f, -0.5f, -0.5f,	0.0f, -1.0f,  0.0f,		1.0f, 0.0f,
		0.0f, -0.5f,  0.5f,		0.0f, -1.0f,  0.0f,		0.5f, 1.0f,
		-0.5f, -0.5f,  -0.5f,	0.0f, -1.0f,  0.0f,		0.0f, 0.0f,

		//Left Face/slanted		//Normals
		-0.5f, -0.5f, -0.5f,	0.894427180f,  0.0f,  -0.447213590f,	0.0f, 0.0f,
		-0.5f, 0.5f,  -0.5f,	0.894427180f,  0.0f,  -0.447213590f,	0.0f, 1.0f,
		0.0f, 0.5f,  0.5f,		0.894427180f,  0.0f,  -0.447213590f,	1.0f, 1.0f,
		-0.5f, -0.5f, -0.5f,	0.894427180f,  0.0f,  -0.447213590f,	0.0f, 0.0f,
		-0.5f, -0.5f, -0.5f,	0.894427180f,  0.0f,  -0.447213590f,	0.0f, 0.0f,
		0.0f, -0.5f,  0.5f,		0.894427180f,  0.0f,  -0.447213590f,	1.0f, 0.0f,
		0.0f, 0.5f,  0.5f,		0.894427180f,  0.0f,  -0.447213590f,	1.0f, 1.0f,
		-0.5f, -0.5f, -0.5f,	0.894427180f,  0.0f,  -0.447213590f,	0.0f, 0.0f,

		//Right Face/slanted	//Normals
		0.0f, 0.5f, 0.5f,		-0.894427180f,  0.0f,  -0.447213590f,		0.0f, 1.0f,
		0.5f, 0.5f, -0.5f,		-0.894427180f,  0.0f,  -0.447213590f,		1.0f, 1.0f,
		0.5f, -0.5f, -0.5f,		-0.894427180f,  0.0f,  -0.447213590f,		1.0f, 0.0f,
		0.0f, 0.5f, 0.5f,		-0.894427180f,  0.0f,  -0.447213590f,		0.0f, 1.0f,
		0.0f, 0.5f, 0.5f,		-0.894427180f,  0.0f,  -0.447213590f,		0.0f, 1.0f,
		0.0f, -0.5f, 0.5f,		-0.894427180f,  0.0f,  -0.447213590f,		0.0f, 0.0f,
		0.5f, -0.5f, -0.5f,		-0.894427180f,  0.0f,  -0.447213590f,		1.0f, 0.0f,
		0.0f, 0.5f, 0.5f,		-0.894427180f,  0.0f,  -0.447213590f,		0.0f, 1.0f,

		//Top Face				//Positive Y Normal		//Texture Coords.
		0.5f, 0.5f, -0.5f,		0.0f,  1.0f,  0.0f,		0.0f, 0.0f,
		0.0f,  0.5f,  0.5f,		0.0f,  1.0f,  0.0f,		0.5f, 1.0f,
		-0.5f,  0.5f, -0.5f,	0.0f,  1.0f,  0.0f,		1.0f, 0.0f,
		0.5f, 0.5f, -0.5f,		0.0f,  1.0f,  0.0f,		0.0f, 0.0f,

	};

	GLuint indices[] = {
		0, 1, 2, // Example indices
		// Add indices for all faces if necessary
	};

	// Correctly Calculate Vertex Count
	size_t vertexCount = sizeof(verts) / sizeof(GLfloat);
	m_PrismMesh.nVertices = static_cast<GLuint>(vertexCount / (FloatsPerVertex + FloatsPerNormal + FloatsPerUV));

	// Convert Raw Arrays to Vectors
	std::vector<GLfloat> vertsVector(std::begin(verts), std::end(verts));
	std::vector<GLuint> indicesVector(std::begin(indices), std::end(indices)); // FIX: Explicitly convert indices to vector

	// Use the new InitializeMesh function
	InitializeMesh(m_PrismMesh, vertsVector, indicesVector);
}

/******************************************
 * LoadPyramid3Mesh
 * ---------------------------------------
 * Generates a 3-sided pyramid mesh with vertex
 * positions, normals, and texture coordinates.
 *
 * - Uses a triangle strip for efficient rendering.
 * - Dynamically calculates face normals.
 * - No index buffer is used; rendering is done with `glDrawArrays`.
 *
 * Correct draw call:
 * glDrawArrays(GL_TRIANGLE_STRIP, 0, m_Pyramid3Mesh.nVertices);
 ******************************************/

void ShapeMeshes::LoadPyramid3Mesh()
{
	constexpr float halfBase = 0.5f; // Half the length of the base
	constexpr float height = 0.5f;  // Height of the pyramid

	// Define vertices programmatically
	std::vector<GLfloat> verts;

	// Helper for normals
	auto calculateNormal = [](float x1, float y1, float z1, float x2, float y2, float z2)->std::array<float, 3> {
		float nx = y1 * z2 - z1 * y2;
		float ny = z1 * x2 - x1 * z2;
		float nz = x1 * y2 - y1 * x2;
		float length = sqrt(nx * nx + ny * ny + nz * nz);
		return { nx / length, ny / length, nz / length };
	};

	// Define the pyramid faces with vertices and normals
	struct Face {
		std::array<float, 3> top;      // Top vertex
		std::array<float, 3> bottom1; // First base vertex
		std::array<float, 3> bottom2; // Second base vertex
		std::array<float, 3> normal;  // Normal vector
	};

	std::vector<Face> faces = {
		// Left face
		{{0.0f, height, 0.0f}, {-halfBase, -height, halfBase}, {0.0f, -height, -halfBase},
		 calculateNormal(-halfBase, -height - height, halfBase - 0.0f, 0.0f, -height - height, -halfBase - halfBase)},
		 // Right face
		 {{0.0f, height, 0.0f}, {0.0f, -height, -halfBase}, {halfBase, -height, halfBase},
		  calculateNormal(0.0f, -height - height, -halfBase - 0.0f, halfBase, -height - height, halfBase - -halfBase)},
		  // Front face
		  {{0.0f, height, 0.0f}, {halfBase, -height, halfBase}, {-halfBase, -height, halfBase},
		   calculateNormal(halfBase, -height - height, halfBase - 0.0f, -halfBase, -height - height, halfBase - halfBase)} };

	for (const auto& face : faces)
	{
		// Top point
		verts.insert(verts.end(), {
			face.top[0], face.top[1], face.top[2],
			face.normal[0], face.normal[1], face.normal[2],
			0.5f, 1.0f });

		// First base vertex
		verts.insert(verts.end(), {
			face.bottom1[0], face.bottom1[1], face.bottom1[2],
			face.normal[0], face.normal[1], face.normal[2],
			0.0f, 0.0f });

		// Second base vertex
		verts.insert(verts.end(), {
			face.bottom2[0], face.bottom2[1], face.bottom2[2],
			face.normal[0], face.normal[1], face.normal[2],
			1.0f, 0.0f });
	}

	// Base (bottom face)
	verts.insert(verts.end(), {
		-halfBase, -height, halfBase, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f,
		halfBase, -height, halfBase, 0.0f, -1.0f, 0.0f, 1.0f, 1.0f,
		0.0f, -height, -halfBase, 0.0f, -1.0f, 0.0f, 0.5f, 0.0f });

	// **No Indices Used**
	m_Pyramid3Mesh.nVertices = verts.size() / (FloatsPerVertex + FloatsPerNormal + FloatsPerUV);
	m_Pyramid3Mesh.nIndices = 0; // No indices, drawing with `glDrawArrays`

	// Use InitializeMesh() without indices
	InitializeMesh(m_Pyramid3Mesh, verts, {}); // Pass an empty vector for indices

}

/******************************************
 * LoadPyramid4Mesh
 * ---------------------------------------
 * Generates a 4-sided pyramid mesh with vertex
 * positions, normals, and texture coordinates.
 *
 * - The base is a square.
 * - The pyramid has 5 faces: 1 square base and 4 triangular sides.
 * - Dynamically calculates face normals for lighting.
 * - Uses `glDrawArrays` for rendering (no index buffer).
 *
 * Correct draw call:
 * glDrawArrays(GL_TRIANGLES, 0, m_Pyramid4Mesh.nVertices);
 ******************************************/

void ShapeMeshes::LoadPyramid4Mesh(float baseSize, float height)
{
	constexpr int FloatsPerVertex = 3;
	constexpr int FloatsPerNormal = 3;
	constexpr int FloatsPerUV = 2;

	float halfBase = baseSize / 2.0f;

	// Vertex data container
	std::vector<GLfloat> verts;

	// Helper lambda to add vertex data
	auto addVertex = [&verts](float px, float py, float pz, float nx, float ny, float nz, float u, float v) {
		verts.insert(verts.end(), { px, py, pz, nx, ny, nz, u, v });
	};

	// Helper for normal calculation
	auto calculateNormal = [](float x1, float y1, float z1, float x2, float y2, float z2)->std::array<float, 3> {
		float nx = y1 * z2 - z1 * y2;
		float ny = z1 * x2 - x1 * z2;
		float nz = x1 * y2 - y1 * x2;
		float length = std::sqrt(nx * nx + ny * ny + nz * nz);
		return { nx / length, ny / length, nz / length };
	};

	// **Bottom face (two triangles)**
	glm::vec3 bottomNormal = { 0.0f, -1.0f, 0.0f };

	addVertex(-halfBase, -halfBase, halfBase, bottomNormal.x, bottomNormal.y, bottomNormal.z, 0.0f, 1.0f);  // Front-left
	addVertex(-halfBase, -halfBase, -halfBase, bottomNormal.x, bottomNormal.y, bottomNormal.z, 0.0f, 0.0f); // Back-left
	addVertex(halfBase, -halfBase, -halfBase, bottomNormal.x, bottomNormal.y, bottomNormal.z, 1.0f, 0.0f); // Back-right

	addVertex(-halfBase, -halfBase, halfBase, bottomNormal.x, bottomNormal.y, bottomNormal.z, 0.0f, 1.0f);  // Front-left
	addVertex(halfBase, -halfBase, -halfBase, bottomNormal.x, bottomNormal.y, bottomNormal.z, 1.0f, 0.0f); // Back-right
	addVertex(halfBase, -halfBase, halfBase, bottomNormal.x, bottomNormal.y, bottomNormal.z, 1.0f, 1.0f); // Front-right

	// **Pyramid faces (triangular sides)**
	struct Face {
		glm::vec3 top;
		glm::vec3 bottomLeft;
		glm::vec3 bottomRight;
	};

	std::vector<Face> faces = {
		{{0.0f, height / 2.0f, 0.0f}, {-halfBase, -halfBase, -halfBase}, {-halfBase, -halfBase, halfBase}},  // Left
		{{0.0f, height / 2.0f, 0.0f}, {halfBase, -halfBase, -halfBase}, {-halfBase, -halfBase, -halfBase}}, // Back
		{{0.0f, height / 2.0f, 0.0f}, {halfBase, -halfBase, halfBase}, {halfBase, -halfBase, -halfBase}},   // Right
		{{0.0f, height / 2.0f, 0.0f}, {-halfBase, -halfBase, halfBase}, {halfBase, -halfBase, halfBase}}    // Front
	};

	for (const auto& face : faces)
	{
		// Calculate normal for the face
		glm::vec3 u = face.bottomRight - face.bottomLeft;
		glm::vec3 v = face.top - face.bottomLeft;
		glm::vec3 normal = glm::normalize(glm::cross(u, v));

		// Add vertices for the triangular face
		addVertex(face.top.x, face.top.y, face.top.z, normal.x, normal.y, normal.z, 0.5f, 1.0f);         // Top vertex
		addVertex(face.bottomLeft.x, face.bottomLeft.y, face.bottomLeft.z, normal.x, normal.y, normal.z, 0.0f, 0.0f); // Bottom-left
		addVertex(face.bottomRight.x, face.bottomRight.y, face.bottomRight.z, normal.x, normal.y, normal.z, 1.0f, 0.0f); // Bottom-right
	}

	std::vector<GLuint> indices; // Empty since using glDrawArrays

	// Store vertex count
	m_Pyramid4Mesh.nVertices = verts.size() / (FloatsPerVertex + FloatsPerNormal + FloatsPerUV);
	m_Pyramid4Mesh.nIndices = 0; // No indices, drawing with `glDrawArrays`

	// Use the centralized InitializeMesh function
	InitializeMesh(m_Pyramid4Mesh, verts, {}); // Pass empty indices vector
}

/******************************************
 * LoadSphereMesh
 * ---------------------------------------
 * Generates a UV sphere mesh using latitude and
 * longitude segment divisions.
 *
 * - Uses parametric equations for vertex positioning.
 * - Computes normals for smooth shading.
 * - Generates texture coordinates for UV mapping.
 * - Uses indexed drawing for efficiency.
 *
 * Params:
 * - latitudeSegments: Number of vertical divisions.
 * - longitudeSegments: Number of horizontal divisions.
 * - radius: Sphere's radius.
 *
 * Correct draw call:
 * glDrawElements(GL_TRIANGLES, m_SphereMesh.nIndices, GL_UNSIGNED_INT, nullptr);
 ******************************************/

void ShapeMeshes::LoadSphereMesh(int latitudeSegments,
	int longitudeSegments,
	float radius)
{
	std::vector<GLfloat> vertices;
	std::vector<GLuint>  indices;

	// --- generate full?sphere vertices ---
	for (int lat = 0; lat <= latitudeSegments; ++lat) {
		float theta = lat * Pi / latitudeSegments;
		float sinTheta = std::sin(theta);
		float cosTheta = std::cos(theta);

		for (int lon = 0; lon <= longitudeSegments; ++lon) {
			float phi = lon * 2 * Pi / longitudeSegments;
			float sinPhi = std::sin(phi);
			float cosPhi = std::cos(phi);

			// position
			float x = radius * sinTheta * cosPhi;
			float y = radius * cosTheta;
			float z = radius * sinTheta * sinPhi;
			// normal
			float nx = sinTheta * cosPhi;
			float ny = cosTheta;
			float nz = sinTheta * sinPhi;
			// uv
			float u = 1.0f - float(lon) / longitudeSegments;
			float v = 1.0f - float(lat) / latitudeSegments;

			vertices.push_back(x);
			vertices.push_back(y);
			vertices.push_back(z);
			vertices.push_back(nx);
			vertices.push_back(ny);
			vertices.push_back(nz);
			vertices.push_back(u);
			vertices.push_back(v);
		}
	}

	// --- generate full?sphere indices ---
	for (int lat = 0; lat < latitudeSegments; ++lat) {
		for (int lon = 0; lon < longitudeSegments; ++lon) {
			int first = lat * (longitudeSegments + 1) + lon;
			int second = first + longitudeSegments + 1;
			// triangle one
			indices.push_back(first);
			indices.push_back(second);
			indices.push_back(first + 1);
			// triangle two
			indices.push_back(second);
			indices.push_back(second + 1);
			indices.push_back(first + 1);
		}
	}

	// store counts and initialize VAO/EBO
	m_SphereMesh.nVertices = GLuint(vertices.size() / (FloatsPerVertex + FloatsPerNormal + FloatsPerUV));
	m_SphereMesh.nIndices = GLuint(indices.size());
	InitializeMesh(m_SphereMesh, vertices, indices);
}

void ShapeMeshes::LoadHemisphereMesh(int latitudeSegments,
	int longitudeSegments,
	float radius)
{
	std::vector<GLfloat> vertices;
	std::vector<GLuint>  indices;

	// we only go up to latitudeSegments/2 so theta [0, pi/2]
	int hemiLatSegments = latitudeSegments / 2;

	// --- generate hemisphere vertices ---
	for (int lat = 0; lat <= hemiLatSegments; ++lat) {
		float theta = lat * Pi / latitudeSegments;  // note divisor is full latitudeSegments
		float sinTheta = std::sin(theta);
		float cosTheta = std::cos(theta);

		for (int lon = 0; lon <= longitudeSegments; ++lon) {
			float phi = lon * 2 * Pi / longitudeSegments;
			float sinPhi = std::sin(phi);
			float cosPhi = std::cos(phi);

			float x = radius * sinTheta * cosPhi;
			float y = radius * cosTheta;
			float z = radius * sinTheta * sinPhi;

			float nx = sinTheta * cosPhi;
			float ny = cosTheta;
			float nz = sinTheta * sinPhi;

			float u = 1.0f - float(lon) / longitudeSegments;
			float v = 1.0f - float(lat) / hemiLatSegments;  // v ? [0,1] over half sphere

			vertices.push_back(x);
			vertices.push_back(y);
			vertices.push_back(z);
			vertices.push_back(nx);
			vertices.push_back(ny);
			vertices.push_back(nz);
			vertices.push_back(u);
			vertices.push_back(v);
		}
	}

	// --- generate hemisphere indices ---
	for (int lat = 0; lat < hemiLatSegments; ++lat) {
		for (int lon = 0; lon < longitudeSegments; ++lon) {
			int first = lat * (longitudeSegments + 1) + lon;
			int second = first + longitudeSegments + 1;

			indices.push_back(first);
			indices.push_back(second);
			indices.push_back(first + 1);

			indices.push_back(second);
			indices.push_back(second + 1);
			indices.push_back(first + 1);
		}
	}

	m_HemisphereMesh.nVertices = GLuint(vertices.size() / (FloatsPerVertex + FloatsPerNormal + FloatsPerUV));
	m_HemisphereMesh.nIndices = GLuint(indices.size());
	InitializeMesh(m_HemisphereMesh, vertices, indices);
}

/******************************************
 * LoadTaperedCylinderMesh
 * ---------------------------------------
 * Generates a tapered cylinder with a variable
 * top and bottom radius. The shape consists of:
 *  - A circular bottom cap using a triangle fan.
 *  - A circular top cap using a triangle fan.
 *  - The sides using a triangle strip.
 *
 * - Computes vertex positions dynamically based on the tapering.
 * - Generates normals for smooth shading.
 * - Uses indexed drawing for efficient rendering.
 *
 * Params:
 * - bottomRadius: Radius of the base.
 * - topRadius: Radius of the top.
 * - height: Height of the tapered cylinder.
 * - numSlices: Number of subdivisions along the circumference.
 *
 * Correct draw calls:
 * glDrawArrays(GL_TRIANGLE_FAN, 0, numSlices + 2);   // Bottom cap
 * glDrawArrays(GL_TRIANGLE_FAN, numSlices + 2, numSlices + 2); // Top cap
 * glDrawArrays(GL_TRIANGLE_STRIP, 2 * (numSlices + 2), numSlices * 2); // Sides
 ******************************************/

void ShapeMeshes::LoadTaperedCylinderMesh(float bottomRadius, float topRadius, float height, int numSlices)
{
	if (numSlices < 3) numSlices = 3;
	m_TaperedCylinderMesh.numSlices = numSlices;

	std::vector<GLfloat> vertices; // interleaved: px,py,pz, nx,ny,nz, u,v   (8 floats)
	std::vector<GLuint>  indices;

	vertices.reserve((2 * (numSlices + 1) /*caps (with centers)*/ + 2 * numSlices /*sides*/) * 8);
	indices.reserve(numSlices * 3 /*bottom*/ + numSlices * 3 /*top*/ + numSlices * 6 /*sides*/);

	const float angleStep = 2.0f * Pi / numSlices;

	// Bottom Cap (normal down)
	const int bottomCenterIndex = static_cast<int>(vertices.size() / 8);
	vertices.insert(vertices.end(), {
		0.0f, 0.0f, 0.0f,   0.0f,-1.0f, 0.0f,   0.5f, 0.5f
		});

	for (int i = 0; i < numSlices; ++i) {
		float a = i * angleStep;
		float x = bottomRadius * std::cos(a);
		float z = bottomRadius * std::sin(a);
		float u = 0.5f + 0.5f * std::cos(a);
		float v = 0.5f + 0.5f * std::sin(a);
		vertices.insert(vertices.end(), { x,0.0f,z,  0.0f,-1.0f,0.0f,  u,v });

		// Triangle fan (center, i, i+1)
		indices.push_back(bottomCenterIndex);
		indices.push_back(bottomCenterIndex + 1 + i);
		indices.push_back(bottomCenterIndex + 1 + ((i + 1) % numSlices));
	}

	// Top Cap (normal up)
	const int topCenterIndex = static_cast<int>(vertices.size() / 8);
	vertices.insert(vertices.end(), {
		0.0f, height, 0.0f,   0.0f, 1.0f, 0.0f,   0.5f, 0.5f
		});

	for (int i = 0; i < numSlices; ++i) {
		float a = i * angleStep;
		float x = topRadius * std::cos(a);
		float z = topRadius * std::sin(a);
		float u = 0.5f + 0.5f * std::cos(a);
		float v = 0.5f + 0.5f * std::sin(a);
		vertices.insert(vertices.end(), { x,height,z,  0.0f,1.0f,0.0f,  u,v });

		// Triangle fan CCW as seen from ABOVE (center, next, current)
		indices.push_back(topCenterIndex);
		indices.push_back(topCenterIndex + 1 + ((i + 1) % numSlices));
		indices.push_back(topCenterIndex + 1 + i);
	}

	// Sides (two verts per slice, two triangles per quad)
	const int sideStartIndex = static_cast<int>(vertices.size() / 8);

	// correct outward normal for a frustum wall: tilt by slope
	const float slope = (bottomRadius - topRadius) / height; // >0 if bottom > top
	for (int i = 0; i < numSlices; ++i) {
		float a = i * angleStep;
		float cb = std::cos(a), sb = std::sin(a);

		// positions
		float xB = bottomRadius * cb, zB = bottomRadius * sb;
		float xT = topRadius * cb, zT = topRadius * sb;

		// outward normal
		glm::vec3 n = glm::normalize(glm::vec3(cb, slope, sb));

		// bottom ring vertex (side)
		vertices.insert(vertices.end(), {
			xB, 0.0f, zB,   n.x, n.y, n.z,   static_cast<float>(i) / numSlices, 1.0f
			});
		// top ring vertex (side)
		vertices.insert(vertices.end(), {
			xT, height, zT, n.x, n.y, n.z,   static_cast<float>(i) / numSlices, 0.0f
			});
	}

	for (int i = 0; i < numSlices; ++i) {
		int iNext = (i + 1) % numSlices;

		GLuint B = static_cast<GLuint>(sideStartIndex + 2 * i);
		GLuint T = B + 1;
		GLuint Bn = static_cast<GLuint>(sideStartIndex + 2 * iNext);
		GLuint Tn = Bn + 1;

		// CCW as seen from outside: (B, Bn, T) and (T, Bn, Tn)
		indices.push_back(B);
		indices.push_back(Bn);
		indices.push_back(T);

		indices.push_back(T);
		indices.push_back(Bn);
		indices.push_back(Tn);
	}

	// Upload
	InitializeMesh(m_TaperedCylinderMesh, vertices, indices);
}


/******************************************
 * LoadTorusMesh
 * ---------------------------------------
 * Generates a torus (donut shape) using two sets
 * of circular loops for vertices and normals.
 *
 * - The main ring (torus major radius) forms a circular path.
 * - A secondary ring (torus minor radius) defines the tube thickness.
 * - Vertices are generated using parametric equations.
 * - Uses indexed drawing for efficient rendering.
 *
 * Params:
 * - mainRadius: Distance from the torus center to the middle of the tube.
 * - tubeRadius: Radius of the tube forming the torus.
 * - mainSegments: Number of circular segments around the torus.
 * - tubeSegments: Number of circular segments around the tube.
 *
 * Correct draw call:
 * glDrawArrays(GL_TRIANGLES, 0, m_TorusMesh.nVertices);
 ******************************************/

void ShapeMeshes::LoadTorusMesh(float mainRadius, float tubeRadius, int mainSegments, int tubeSegments)
{
	// Validate input parameters
	mainSegments = std::max(3, mainSegments);
	tubeSegments = std::max(3, tubeSegments);
	tubeRadius = std::max(0.01f, tubeRadius);

	float mainSegmentStep = 2.0f * Pi / mainSegments;
	float tubeSegmentStep = 2.0f * Pi / tubeSegments;

	std::vector<GLfloat> vertices;
	std::vector<GLuint> indices;

	// Generate vertices and normals
	for (int i = 0; i <= mainSegments; ++i) {
		float mainAngle = i * mainSegmentStep;
		float cosMain = cos(mainAngle);
		float sinMain = sin(mainAngle);

		for (int j = 0; j <= tubeSegments; ++j) {
			float tubeAngle = j * tubeSegmentStep;
			float cosTube = cos(tubeAngle);
			float sinTube = sin(tubeAngle);

			// Vertex position
			float x = (mainRadius + tubeRadius * cosTube) * cosMain;
			float y = (mainRadius + tubeRadius * cosTube) * sinMain;
			float z = tubeRadius * sinTube;

			// Normal vector
			glm::vec3 center(mainRadius * cosMain, mainRadius * sinMain, 0.0f);
			glm::vec3 vertex(x, y, z);
			glm::vec3 normal = glm::normalize(vertex - center);

			// Texture coordinates
			float u = (float)i / mainSegments;
			float v = (float)j / tubeSegments;

			// Store interleaved vertex data
			vertices.push_back(x);    // Position x
			vertices.push_back(y);    // Position y
			vertices.push_back(z);    // Position z
			vertices.push_back(normal.x); // Normal x
			vertices.push_back(normal.y); // Normal y
			vertices.push_back(normal.z); // Normal z
			vertices.push_back(u);    // Texture u
			vertices.push_back(v);    // Texture v
		}
	}

	// Generate indices for triangle strips
	for (int i = 0; i < mainSegments; ++i) {
		for (int j = 0; j < tubeSegments; ++j) {
			int current = i * (tubeSegments + 1) + j;
			int next = (i + 1) * (tubeSegments + 1) + j;

			// First triangle
			indices.push_back(current);
			indices.push_back(next);
			indices.push_back(current + 1);

			// Second triangle
			indices.push_back(current + 1);
			indices.push_back(next);
			indices.push_back(next + 1);
		}
	}

	// Store vertex and index counts
	m_TorusMesh.nVertices = static_cast<GLuint>(vertices.size() / (FloatsPerVertex + FloatsPerNormal + FloatsPerUV));
	m_TorusMesh.nIndices = static_cast<GLuint>(indices.size());

	// Use the centralized InitializeMesh function
	InitializeMesh(m_TorusMesh, vertices, indices);
}

///////////////////////////////////////////////////
//	LoadExtraTorusMesh1()
//
//	Create a torus mesh by specifying the vertices and 
//  store it in a VAO/VBO.  The normals and texture
//  coordinates are also set.
//
//	Correct triangle drawing command:
//
//	glDrawArrays(GL_TRIANGLES, 0, meshes.gExtraTorusMesh1.nVertices);
///////////////////////////////////////////////////
void ShapeMeshes::LoadExtraTorusMesh1(float thickness)
{
	int _mainSegments = 30;
	int _tubeSegments = 30;
	float _mainRadius = 1.0f;
	float _tubeRadius = .1f;

	if (thickness <= 1.0)
	{
		_tubeRadius = thickness;
	}

	auto mainSegmentAngleStep = glm::radians(360.0f / float(_mainSegments));
	auto tubeSegmentAngleStep = glm::radians(360.0f / float(_tubeSegments));

	std::vector<glm::vec3> vertex_list;
	std::vector<std::vector<glm::vec3>> segments_list;
	std::vector<glm::vec2> texture_coords;
	glm::vec3 center(0.0f, 0.0f, 0.0f);
	glm::vec3 normal;
	glm::vec3 vertex;
	glm::vec2 text_coord;

	// generate the torus vertices
	auto currentMainSegmentAngle = 0.0f;
	for (auto i = 0; i < _mainSegments; i++)
	{
		// Calculate sine and cosine of main segment angle
		auto sinMainSegment = sin(currentMainSegmentAngle);
		auto cosMainSegment = cos(currentMainSegmentAngle);
		auto currentTubeSegmentAngle = 0.0f;
		std::vector<glm::vec3> segment_points;
		for (auto j = 0; j < _tubeSegments; j++)
		{
			// Calculate sine and cosine of tube segment angle
			auto sinTubeSegment = sin(currentTubeSegmentAngle);
			auto cosTubeSegment = cos(currentTubeSegmentAngle);

			// Calculate vertex position on the surface of torus
			auto surfacePosition = glm::vec3(
				(_mainRadius + _tubeRadius * cosTubeSegment) * cosMainSegment,
				(_mainRadius + _tubeRadius * cosTubeSegment) * sinMainSegment,
				_tubeRadius * sinTubeSegment);

			//vertex_list.push_back(surfacePosition);
			segment_points.push_back(surfacePosition);

			// Update current tube angle
			currentTubeSegmentAngle += tubeSegmentAngleStep;
		}
		segments_list.push_back(segment_points);
		segment_points.clear();

		// Update main segment angle
		currentMainSegmentAngle += mainSegmentAngleStep;
	}

	float horizontalStep = 1.0 / _mainSegments;
	float verticalStep = 1.0 / _tubeSegments;
	float u = 0.0;
	float v = 0.0;

	// connect the various segments together, forming triangles
	for (int i = 0; i < _mainSegments; i++)
	{
		for (int j = 0; j < _tubeSegments; j++)
		{
			if (((i + 1) < _mainSegments) && ((j + 1) < _tubeSegments))
			{
				vertex_list.push_back(segments_list[i][j]);
				texture_coords.push_back(glm::vec2(u, v));
				vertex_list.push_back(segments_list[i][j + 1]);
				texture_coords.push_back(glm::vec2(u, v + verticalStep));
				vertex_list.push_back(segments_list[i + 1][j + 1]);
				texture_coords.push_back(glm::vec2(u + horizontalStep, v + verticalStep));
				vertex_list.push_back(segments_list[i][j]);
				texture_coords.push_back(glm::vec2(u, v));
				vertex_list.push_back(segments_list[i + 1][j]);
				texture_coords.push_back(glm::vec2(u + horizontalStep, v));
				vertex_list.push_back(segments_list[i + 1][j + 1]);
				texture_coords.push_back(glm::vec2(u + horizontalStep, v - verticalStep));
				vertex_list.push_back(segments_list[i][j]);
				texture_coords.push_back(glm::vec2(u, v));
			}
			else
			{
				if (((i + 1) == _mainSegments) && ((j + 1) == _tubeSegments))
				{
					vertex_list.push_back(segments_list[i][j]);
					texture_coords.push_back(glm::vec2(u, v));
					vertex_list.push_back(segments_list[i][0]);
					texture_coords.push_back(glm::vec2(u, 0));
					vertex_list.push_back(segments_list[0][0]);
					texture_coords.push_back(glm::vec2(0, 0));
					vertex_list.push_back(segments_list[i][j]);
					texture_coords.push_back(glm::vec2(u, v));
					vertex_list.push_back(segments_list[0][j]);
					texture_coords.push_back(glm::vec2(0, v));
					vertex_list.push_back(segments_list[0][0]);
					texture_coords.push_back(glm::vec2(0, 0));
					vertex_list.push_back(segments_list[i][j]);
					texture_coords.push_back(glm::vec2(u, v));
				}
				else if ((i + 1) == _mainSegments)
				{
					vertex_list.push_back(segments_list[i][j]);
					texture_coords.push_back(glm::vec2(u, v));
					vertex_list.push_back(segments_list[i][j + 1]);
					texture_coords.push_back(glm::vec2(u, v + verticalStep));
					vertex_list.push_back(segments_list[0][j + 1]);
					texture_coords.push_back(glm::vec2(0, v + verticalStep));
					vertex_list.push_back(segments_list[i][j]);
					texture_coords.push_back(glm::vec2(u, v));
					vertex_list.push_back(segments_list[0][j]);
					texture_coords.push_back(glm::vec2(0, v));
					vertex_list.push_back(segments_list[0][j + 1]);
					texture_coords.push_back(glm::vec2(0, v + verticalStep));
					vertex_list.push_back(segments_list[i][j]);
					texture_coords.push_back(glm::vec2(u, v));
				}
				else if ((j + 1) == _tubeSegments)
				{
					vertex_list.push_back(segments_list[i][j]);
					texture_coords.push_back(glm::vec2(u, v));
					vertex_list.push_back(segments_list[i][0]);
					texture_coords.push_back(glm::vec2(u, 0));
					vertex_list.push_back(segments_list[i + 1][0]);
					texture_coords.push_back(glm::vec2(u + horizontalStep, 0));
					vertex_list.push_back(segments_list[i][j]);
					texture_coords.push_back(glm::vec2(u, v));
					vertex_list.push_back(segments_list[i + 1][j]);
					texture_coords.push_back(glm::vec2(u + horizontalStep, v));
					vertex_list.push_back(segments_list[i + 1][0]);
					texture_coords.push_back(glm::vec2(u + horizontalStep, 0));
					vertex_list.push_back(segments_list[i][j]);
					texture_coords.push_back(glm::vec2(u, v));
				}

			}
			v += verticalStep;
		}
		v = 0.0;
		u += horizontalStep;
	}

	std::vector<GLfloat> combined_values;

	// combine interleaved vertices, normals, and texture coords
	for (int i = 0; i < vertex_list.size(); i++)
	{
		vertex = vertex_list[i];
		normal = normalize(vertex);

		text_coord = texture_coords[i];
		combined_values.push_back(vertex.x);
		combined_values.push_back(vertex.y);
		combined_values.push_back(vertex.z);
		combined_values.push_back(normal.x);
		combined_values.push_back(normal.y);
		combined_values.push_back(normal.z);
		combined_values.push_back(text_coord.x);
		combined_values.push_back(text_coord.y);
	}

	// store vertex and index count
	m_ExtraTorusMesh1.nVertices = vertex_list.size();
	m_ExtraTorusMesh1.nIndices = 0;

	// Create VAO
	glGenVertexArrays(1, &m_ExtraTorusMesh1.vao); // we can also generate multiple VAOs or buffers at the same time
	glBindVertexArray(m_ExtraTorusMesh1.vao);

	// Create VBOs
	glGenBuffers(1, m_ExtraTorusMesh1.vbos);
	glBindBuffer(GL_ARRAY_BUFFER, m_ExtraTorusMesh1.vbos[0]); // Activates the buffer
	glBufferData(GL_ARRAY_BUFFER, sizeof(GLfloat) * combined_values.size(), combined_values.data(), GL_STATIC_DRAW); // Sends vertex or coordinate data to the GPU

	if (m_bMemoryLayoutDone == false)
	{
		SetShaderMemoryLayout();
	}
}

///////////////////////////////////////////////////
//	LoadExtraTorusMesh2()
//
//	Create a torus mesh by specifying the vertices and 
//  store it in a VAO/VBO.  The normals and texture
//  coordinates are also set.
//
//	Correct triangle drawing command:
//
//	glDrawArrays(GL_TRIANGLES, 0, meshes.gExtraTorusMesh1.nVertices);
///////////////////////////////////////////////////
void ShapeMeshes::LoadExtraTorusMesh2(float thickness)
{
	int _mainSegments = 30;
	int _tubeSegments = 30;
	float _mainRadius = 1.0f;
	float _tubeRadius = .1f;

	if (thickness <= 1.0)
	{
		_tubeRadius = thickness;
	}

	auto mainSegmentAngleStep = glm::radians(360.0f / float(_mainSegments));
	auto tubeSegmentAngleStep = glm::radians(360.0f / float(_tubeSegments));

	std::vector<glm::vec3> vertex_list;
	std::vector<std::vector<glm::vec3>> segments_list;
	std::vector<glm::vec2> texture_coords;
	glm::vec3 center(0.0f, 0.0f, 0.0f);
	glm::vec3 normal;
	glm::vec3 vertex;
	glm::vec2 text_coord;

	// generate the torus vertices
	auto currentMainSegmentAngle = 0.0f;
	for (auto i = 0; i < _mainSegments; i++)
	{
		// Calculate sine and cosine of main segment angle
		auto sinMainSegment = sin(currentMainSegmentAngle);
		auto cosMainSegment = cos(currentMainSegmentAngle);
		auto currentTubeSegmentAngle = 0.0f;
		std::vector<glm::vec3> segment_points;
		for (auto j = 0; j < _tubeSegments; j++)
		{
			// Calculate sine and cosine of tube segment angle
			auto sinTubeSegment = sin(currentTubeSegmentAngle);
			auto cosTubeSegment = cos(currentTubeSegmentAngle);

			// Calculate vertex position on the surface of torus
			auto surfacePosition = glm::vec3(
				(_mainRadius + _tubeRadius * cosTubeSegment) * cosMainSegment,
				(_mainRadius + _tubeRadius * cosTubeSegment) * sinMainSegment,
				_tubeRadius * sinTubeSegment);

			//vertex_list.push_back(surfacePosition);
			segment_points.push_back(surfacePosition);

			// Update current tube angle
			currentTubeSegmentAngle += tubeSegmentAngleStep;
		}
		segments_list.push_back(segment_points);
		segment_points.clear();

		// Update main segment angle
		currentMainSegmentAngle += mainSegmentAngleStep;
	}

	float horizontalStep = 1.0 / _mainSegments;
	float verticalStep = 1.0 / _tubeSegments;
	float u = 0.0;
	float v = 0.0;

	// connect the various segments together, forming triangles
	for (int i = 0; i < _mainSegments; i++)
	{
		for (int j = 0; j < _tubeSegments; j++)
		{
			if (((i + 1) < _mainSegments) && ((j + 1) < _tubeSegments))
			{
				vertex_list.push_back(segments_list[i][j]);
				texture_coords.push_back(glm::vec2(u, v));
				vertex_list.push_back(segments_list[i][j + 1]);
				texture_coords.push_back(glm::vec2(u, v + verticalStep));
				vertex_list.push_back(segments_list[i + 1][j + 1]);
				texture_coords.push_back(glm::vec2(u + horizontalStep, v + verticalStep));
				vertex_list.push_back(segments_list[i][j]);
				texture_coords.push_back(glm::vec2(u, v));
				vertex_list.push_back(segments_list[i + 1][j]);
				texture_coords.push_back(glm::vec2(u + horizontalStep, v));
				vertex_list.push_back(segments_list[i + 1][j + 1]);
				texture_coords.push_back(glm::vec2(u + horizontalStep, v - verticalStep));
				vertex_list.push_back(segments_list[i][j]);
				texture_coords.push_back(glm::vec2(u, v));
			}
			else
			{
				if (((i + 1) == _mainSegments) && ((j + 1) == _tubeSegments))
				{
					vertex_list.push_back(segments_list[i][j]);
					texture_coords.push_back(glm::vec2(u, v));
					vertex_list.push_back(segments_list[i][0]);
					texture_coords.push_back(glm::vec2(u, 0));
					vertex_list.push_back(segments_list[0][0]);
					texture_coords.push_back(glm::vec2(0, 0));
					vertex_list.push_back(segments_list[i][j]);
					texture_coords.push_back(glm::vec2(u, v));
					vertex_list.push_back(segments_list[0][j]);
					texture_coords.push_back(glm::vec2(0, v));
					vertex_list.push_back(segments_list[0][0]);
					texture_coords.push_back(glm::vec2(0, 0));
					vertex_list.push_back(segments_list[i][j]);
					texture_coords.push_back(glm::vec2(u, v));
				}
				else if ((i + 1) == _mainSegments)
				{
					vertex_list.push_back(segments_list[i][j]);
					texture_coords.push_back(glm::vec2(u, v));
					vertex_list.push_back(segments_list[i][j + 1]);
					texture_coords.push_back(glm::vec2(u, v + verticalStep));
					vertex_list.push_back(segments_list[0][j + 1]);
					texture_coords.push_back(glm::vec2(0, v + verticalStep));
					vertex_list.push_back(segments_list[i][j]);
					texture_coords.push_back(glm::vec2(u, v));
					vertex_list.push_back(segments_list[0][j]);
					texture_coords.push_back(glm::vec2(0, v));
					vertex_list.push_back(segments_list[0][j + 1]);
					texture_coords.push_back(glm::vec2(0, v + verticalStep));
					vertex_list.push_back(segments_list[i][j]);
					texture_coords.push_back(glm::vec2(u, v));
				}
				else if ((j + 1) == _tubeSegments)
				{
					vertex_list.push_back(segments_list[i][j]);
					texture_coords.push_back(glm::vec2(u, v));
					vertex_list.push_back(segments_list[i][0]);
					texture_coords.push_back(glm::vec2(u, 0));
					vertex_list.push_back(segments_list[i + 1][0]);
					texture_coords.push_back(glm::vec2(u + horizontalStep, 0));
					vertex_list.push_back(segments_list[i][j]);
					texture_coords.push_back(glm::vec2(u, v));
					vertex_list.push_back(segments_list[i + 1][j]);
					texture_coords.push_back(glm::vec2(u + horizontalStep, v));
					vertex_list.push_back(segments_list[i + 1][0]);
					texture_coords.push_back(glm::vec2(u + horizontalStep, 0));
					vertex_list.push_back(segments_list[i][j]);
					texture_coords.push_back(glm::vec2(u, v));
				}

			}
			v += verticalStep;
		}
		v = 0.0;
		u += horizontalStep;
	}

	std::vector<GLfloat> combined_values;

	// combine interleaved vertices, normals, and texture coords
	for (int i = 0; i < vertex_list.size(); i++)
	{
		vertex = vertex_list[i];
		normal = normalize(vertex);

		text_coord = texture_coords[i];
		combined_values.push_back(vertex.x);
		combined_values.push_back(vertex.y);
		combined_values.push_back(vertex.z);
		combined_values.push_back(normal.x);
		combined_values.push_back(normal.y);
		combined_values.push_back(normal.z);
		combined_values.push_back(text_coord.x);
		combined_values.push_back(text_coord.y);
	}

	// store vertex and index count
	m_ExtraTorusMesh2.nVertices = vertex_list.size();
	m_ExtraTorusMesh2.nIndices = 0;

	// Create VAO
	glGenVertexArrays(1, &m_ExtraTorusMesh2.vao); // we can also generate multiple VAOs or buffers at the same time
	glBindVertexArray(m_ExtraTorusMesh2.vao);

	// Create VBOs
	glGenBuffers(1, m_ExtraTorusMesh2.vbos);
	glBindBuffer(GL_ARRAY_BUFFER, m_ExtraTorusMesh2.vbos[0]); // Activates the buffer
	glBufferData(GL_ARRAY_BUFFER, sizeof(GLfloat) * combined_values.size(), combined_values.data(), GL_STATIC_DRAW); // Sends vertex or coordinate data to the GPU

	if (m_bMemoryLayoutDone == false)
	{
		SetShaderMemoryLayout();
	}
}

/******************************************
 * LoadSpringMesh
 * ---------------------------------------
 * Generates a 3D helical spring mesh by sweeping
 * a tube along a helical path while aligning it
 * perpendicular to the trajectory.
 *
 * Params:
 * - mainRadius: Distance from the center to the middle of the coil.
 * - tubeRadius: Radius of the coil's cross-section.
 * - mainSegments: Number of loops in the spring.
 * - tubeSegments: Number of subdivisions around the tube.
 * - springLength: Total vertical height of the spring.
 ******************************************/
void ShapeMeshes::LoadSpringMesh(float mainRadius, float tubeRadius, int mainSegments, int tubeSegments, float springLength)
{
	// Ensure valid parameters
	mainSegments = std::max(1, mainSegments);
	tubeSegments = std::max(8, tubeSegments);  // More segments for smooth coil

	std::vector<GLfloat> vertices;
	std::vector<GLuint> indices;

	float mainAngleStep = (2.0f * Pi) / tubeSegments; // Angle step per tube segment
	float heightStep = springLength / (mainSegments * tubeSegments); // Height per step
	float coilPitch = springLength / mainSegments; // Distance between loops

	// Generate vertices for the helical tube with correct orientation
	for (int i = 0; i <= mainSegments * tubeSegments; ++i)
	{
		float mainAngle = i * mainAngleStep;  // Helix angle
		float centerX = mainRadius * cos(mainAngle); // Helix X
		float centerY = mainRadius * sin(mainAngle); // Helix Y
		float centerZ = i * heightStep;  // Helix height (Z)

		// Tangent direction of the helix (approximate with forward difference)
		glm::vec3 tangent(
			-mainRadius * sin(mainAngle),  // dx/d(theta)
			mainRadius * cos(mainAngle),   // dy/d(theta)
			heightStep                     // dz/d(theta)
		);
		tangent = glm::normalize(tangent); // Normalize tangent vector

		// Compute perpendicular vectors for tube alignment
		glm::vec3 normal = glm::normalize(glm::vec3(-tangent.y, tangent.x, 0)); // Perpendicular to tangent
		glm::vec3 binormal = glm::cross(tangent, normal); // Second perpendicular direction

		// Generate circular cross-section along the helix
		for (int j = 0; j <= tubeSegments; ++j)
		{
			float tubeAngle = j * 2.0f * Pi / tubeSegments; // Circular angle
			float tx = tubeRadius * cos(tubeAngle);
			float ty = tubeRadius * sin(tubeAngle);

			// Compute final position using the normal/binormal basis
			glm::vec3 point = glm::vec3(centerX, centerY, centerZ) + normal * tx + binormal * ty;

			// Compute normal vector for shading
			glm::vec3 normalVector = glm::normalize(normal * tx + binormal * ty);

			// Texture coordinates
			float u = (float)i / (mainSegments * tubeSegments);
			float v = (float)j / tubeSegments;

			// Store vertex data (position, normal, texture)
			vertices.insert(vertices.end(), { point.x, point.y, point.z, normalVector.x, normalVector.y, normalVector.z, u, v });
		}
	}

	// Generate indices for triangle strips
	for (int i = 0; i < mainSegments * tubeSegments; ++i)
	{
		for (int j = 0; j < tubeSegments; ++j)
		{
			int current = i * (tubeSegments + 1) + j;
			int next = (i + 1) * (tubeSegments + 1) + j;

			// First triangle
			indices.push_back(current);
			indices.push_back(next);
			indices.push_back(current + 1);

			// Second triangle
			indices.push_back(current + 1);
			indices.push_back(next);
			indices.push_back(next + 1);
		}
	}

	// Store vertex and index counts
	m_SpringMesh.nVertices = static_cast<GLuint>(vertices.size() / (FloatsPerVertex + FloatsPerNormal + FloatsPerUV));
	m_SpringMesh.nIndices = static_cast<GLuint>(indices.size());

	// Use the centralized InitializeMesh function
	InitializeMesh(m_SpringMesh, vertices, indices);
}

/******************************************
 * LoadTubeMesh
 * ---------------------------------------
 * Generates a tube mesh (hollow cylinder)
 * with specified thickness by creating:
 * - An outer cylinder.
 * - An inner cylinder (inverted normals).
 * - Top and bottom **ring-shaped** caps.
 *
 * Params:
 * - outerRadius: Outer radius of the tube.
 * - innerRadius: Inner radius of the tube.
 * - height: Height of the tube.
 * - numSlices: Number of radial subdivisions.
 *
 * Correct draw call:
 * glDrawElements(GL_TRIANGLES, m_TubeMesh.nIndices, GL_UNSIGNED_INT, nullptr);
 ******************************************/
void ShapeMeshes::LoadTubeMesh(float outerRadius, float innerRadius, float height, int numSlices)
{
	if (numSlices < 3) numSlices = 3;
	m_TubeMesh.numSlices = numSlices;

	std::vector<GLfloat> vertices;
	std::vector<GLuint> indices;

	float angleStep = 2.0f * Pi / static_cast<float>(numSlices);

	/*** Generate Outer and Inner Ring Vertices ***/
	for (int i = 0; i <= numSlices; ++i) {
		float angle = i * angleStep;
		float x = cos(angle);
		float z = sin(angle);

		// Outer ring (bottom and top)
		vertices.insert(vertices.end(), { outerRadius * x, 0.0f, outerRadius * z, 0.0f, -1.0f, 0.0f, static_cast<float>(i) / numSlices, 1.0f });
		vertices.insert(vertices.end(), { outerRadius * x, height, outerRadius * z, 0.0f, 1.0f, 0.0f, static_cast<float>(i) / numSlices, 0.0f });

		// Inner ring (bottom and top)
		vertices.insert(vertices.end(), { innerRadius * x, 0.0f, innerRadius * z, 0.0f, -1.0f, 0.0f, static_cast<float>(i) / numSlices, 1.0f });
		vertices.insert(vertices.end(), { innerRadius * x, height, innerRadius * z, 0.0f, 1.0f, 0.0f, static_cast<float>(i) / numSlices, 0.0f });
	}

	/*** Generate Outer and Inner Walls ***/
	for (int i = 0; i < numSlices; ++i) {
		GLuint outerBottom1 = static_cast<GLuint>(i * 4);
		GLuint outerTop1 = outerBottom1 + 1;
		GLuint outerBottom2 = outerBottom1 + 4;
		GLuint outerTop2 = outerTop1 + 4;

		GLuint innerBottom1 = outerBottom1 + 2;
		GLuint innerTop1 = outerTop1 + 2;
		GLuint innerBottom2 = innerBottom1 + 4;
		GLuint innerTop2 = innerTop1 + 4;

		// Outer wall
		indices.insert(indices.end(), { outerBottom1, outerBottom2, outerTop1 });
		indices.insert(indices.end(), { outerTop1, outerBottom2, outerTop2 });

		// Inner wall (inverted normal)
		indices.insert(indices.end(), { innerBottom1, innerTop1, innerBottom2 });
		indices.insert(indices.end(), { innerTop1, innerTop2, innerBottom2 });
	}

	/*** Generate Ring-shaped End Caps ***/
	for (int i = 0; i < numSlices; ++i) {
		GLuint outerBottom1 = static_cast<GLuint>(i * 4);
		GLuint innerBottom1 = outerBottom1 + 2;
		GLuint outerBottom2 = outerBottom1 + 4;
		GLuint innerBottom2 = innerBottom1 + 4;

		GLuint outerTop1 = outerBottom1 + 1;
		GLuint innerTop1 = innerBottom1 + 1;
		GLuint outerTop2 = outerBottom2 + 1;
		GLuint innerTop2 = innerBottom2 + 1;

		// Bottom cap ring
		indices.insert(indices.end(), { outerBottom1, outerBottom2, innerBottom1 });
		indices.insert(indices.end(), { innerBottom1, outerBottom2, innerBottom2 });

		// Top cap ring
		indices.insert(indices.end(), { innerTop1, outerTop1, innerTop2 });
		indices.insert(indices.end(), { innerTop2, outerTop1, outerTop2 });
	}

	/*** Store Vertex and Index Counts ***/
	m_TubeMesh.nVertices = static_cast<GLuint>(vertices.size() / (FloatsPerVertex + FloatsPerNormal + FloatsPerUV));
	m_TubeMesh.nIndices = static_cast<GLuint>(indices.size());

	/*** Initialize Mesh ***/
	InitializeMesh(m_TubeMesh, vertices, indices);
}

/******************************************
 * DrawTorusMesh
 * ---------------------------------------
 * Binds the torus mesh’s VAO and renders it
 * using indexed drawing. Supports wireframe mode.
 *
 * - Uses glDrawElements to render the torus efficiently.
 * - Ensures VAO is bound before drawing and unbound afterward.
 *
 * Params:
 * - wireframe: (bool) If true, renders as wireframe.
 ******************************************/

void ShapeMeshes::DrawBoxMesh(bool wireframe) const
{
	if (m_BoxMesh.vao == 0 || m_BoxMesh.nIndices == 0) {
		std::cerr << "Error: Torus mesh not initialized properly." << std::endl;
		return;
	}

	SetWireframeMode(wireframe);

	glBindVertexArray(m_BoxMesh.vao);
	glDrawElements(GL_TRIANGLES, m_BoxMesh.nIndices, GL_UNSIGNED_INT, nullptr);
	glBindVertexArray(0);
}

/******************************************
 * DrawBoxMeshSide
 * ---------------------------------------
 * Binds the box mesh’s VAO and renders only
 * the specified face using indexed drawing.
 * Supports wireframe mode.
 *
 * - Each face is made of two triangles (6 indices).
 * - Uses index offsets to select the correct face.
 * - Ensures VAO is bound before drawing and unbound afterward.
 *
 * Params:
 * - side: (BoxSide) Specifies which face to render.
 * - wireframe: (bool) If true, renders as wireframe.
 ******************************************/

void ShapeMeshes::DrawBoxMeshSide(BoxSide side, bool wireframe) {
	if (m_BoxMesh.vao == 0 || m_BoxMesh.nIndices == 0) {
		std::cerr << "Error: BoxSide mesh not initialized properly." << std::endl;
		return;
	}

	// Set polygon mode based on wireframe parameter
	SetWireframeMode(wireframe);

	glBindVertexArray(m_BoxMesh.vao);

	// Each face of the box consists of two triangles (6 indices per face)
	constexpr int indicesPerFace = 6;

	// Define index offsets for each face
	int offset = 0;
	switch (side) {
	case BoxSide::front:   offset = 0 * indicesPerFace; break;
	case BoxSide::back:    offset = 1 * indicesPerFace; break;
	case BoxSide::left:    offset = 2 * indicesPerFace; break;
	case BoxSide::right:   offset = 3 * indicesPerFace; break;
	case BoxSide::top:     offset = 4 * indicesPerFace; break;
	case BoxSide::bottom:  offset = 5 * indicesPerFace; break;
	default:
		std::cerr << "Error: Invalid BoxSide specified." << std::endl;
		return;
	}

	// Draw only the selected face using indexed drawing
	glDrawElements(GL_TRIANGLES, indicesPerFace, GL_UNSIGNED_INT, (void*)(offset * sizeof(GLuint)));

	glBindVertexArray(0);
}

/******************************************
 * DrawConeMesh
 * ---------------------------------------
 * Binds the cone mesh's VAO and renders the full
 * cone. Optionally draws the bottom cap.
 * Supports wireframe mode.
 *
 * Params:
 * - bDrawBottom: (bool) If true, renders the base cap.
 * - wireframe: (bool) If true, renders as wireframe.
 ******************************************/

void ShapeMeshes::DrawConeMesh(bool bDrawBottom, bool wireframe)
{
	if (!m_ConeMesh.vao) return;

	SetWireframeMode(wireframe);
	glBindVertexArray(m_ConeMesh.vao);

	int bottomCount = m_ConeMesh.numSlices * 3;       // one tri per slice
	int sideCount = m_ConeMesh.numSlices * 3;       // same

	// Disable culling if you still see a seam:
	// glDisable(GL_CULL_FACE);

	// draw bottom
	if (bDrawBottom)
		glDrawElements(GL_TRIANGLES, bottomCount, GL_UNSIGNED_INT, 0);

	// draw sides (offset by bottom indices)
	glDrawElements(GL_TRIANGLES,
		sideCount,
		GL_UNSIGNED_INT,
		(void*)(bottomCount * sizeof(GLuint)));

	// re?enable if you turned it off
	// glEnable(GL_CULL_FACE);

	glBindVertexArray(0);
}



/**************************************
 * Draw Partial Cone Mesh
 **************************************/
void ShapeMeshes::DrawPartialConeMesh(float radius,
	float height,
	int numSlices,
	float arcDegrees,
	bool wireframe)
{
	// --- Validate input ---
	if (numSlices < 3) numSlices = 3;
	arcDegrees = glm::clamp(arcDegrees, 0.0f, 360.0f);

	// --- Prepare geometry containers ---
	std::vector<GLfloat> vertices;
	std::vector<GLuint>  indices;

	float arcRadians = glm::radians(arcDegrees);
	float angleStep = arcRadians / numSlices;
	float halfArc = arcRadians * 0.5f;

	// --- Build vertices (bottom + apex) with proper normals ---
	for (int i = 0; i <= numSlices; ++i) {
		float angle = -halfArc + i * angleStep;
		float x = radius * cos(angle);
		float z = radius * sin(angle);
		float u = float(i) / numSlices;
		// compute correct side normal
		glm::vec3 n = glm::normalize(glm::vec3(cos(angle), radius / height, sin(angle)));

		// bottom vertex
		vertices.insert(vertices.end(), {
			x, 0.0f, z,               // pos
			n.x, n.y, n.z,            // normal
			u, 1.0f                   // uv
			});
		// apex vertex
		vertices.insert(vertices.end(), {
			0.0f, height, 0.0f,       // pos
			n.x, n.y, n.z,            // normal
			u, 0.0f                   // uv
			});
	}

	// --- Build side faces as indexed triangles ---
	// two triangles per slice
	for (int i = 0; i < numSlices; ++i) {
		int b0 = 2 * i;       // bottom vertex i
		int a0 = b0 + 1;    // apex    vertex i
		int b1 = 2 * (i + 1);   // bottom vertex i+1
		int a1 = b1 + 1;    // apex    vertex i+1

		// triangle 1: bottom(i), apex(i+1), apex(i)
		indices.push_back(b0);
		indices.push_back(a1);
		indices.push_back(a0);

		// triangle 2: bottom(i), bottom(i+1), apex(i+1)
		indices.push_back(b0);
		indices.push_back(b1);
		indices.push_back(a1);
	}

	// Optional: close ends with two triangles (uncomment to cap the open edges)
	/*
	// cap at start
	indices.insert(indices.end(), { 1, 0, 2*numSlices });
	// cap at end
	indices.insert(indices.end(), { 1, 2*numSlices+1, 2*numSlices });
	*/

	// --- Upload to GPU once per draw (or better: cache in Load function) ---
	GLuint VAO, VBO, EBO;
	glGenVertexArrays(1, &VAO);
	glGenBuffers(1, &VBO);
	glGenBuffers(1, &EBO);

	glBindVertexArray(VAO);

	// vertex buffer
	glBindBuffer(GL_ARRAY_BUFFER, VBO);
	glBufferData(GL_ARRAY_BUFFER,
		vertices.size() * sizeof(GLfloat),
		vertices.data(),
		GL_STATIC_DRAW);

	// index buffer
	glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
	glBufferData(GL_ELEMENT_ARRAY_BUFFER,
		indices.size() * sizeof(GLuint),
		indices.data(),
		GL_STATIC_DRAW);

	// attribute setup: pos(0), normal(1), uv(2)
	constexpr GLsizei STRIDE = 8 * sizeof(GLfloat);
	glEnableVertexAttribArray(0);
	glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, STRIDE, (void*)0);
	glEnableVertexAttribArray(1);
	glVertexAttribPointer(1, 3, GL_FLOAT, GL_FALSE, STRIDE, (void*)(3 * sizeof(GLfloat)));
	glEnableVertexAttribArray(2);
	glVertexAttribPointer(2, 2, GL_FLOAT, GL_FALSE, STRIDE, (void*)(6 * sizeof(GLfloat)));

	// --- Draw ---
	SetWireframeMode(wireframe);
	glBindVertexArray(VAO);
	glDrawElements(GL_TRIANGLES,
		static_cast<GLsizei>(indices.size()),
		GL_UNSIGNED_INT,
		0);

	// --- Cleanup ---
	glBindVertexArray(0);
	glDeleteBuffers(1, &EBO);
	glDeleteBuffers(1, &VBO);
	glDeleteVertexArrays(1, &VAO);
}


/******************************************
 * DrawCylinderMesh
 * ---------------------------------------
 * Binds the cylinder mesh's VAO and renders the
 * cylinder's top cap, bottom cap, and sides based
 * on the given parameters.
 * Supports wireframe mode.
 *
 * Params:
 * - bDrawTop: (bool) If true, renders the top cap.
 * - bDrawBottom: (bool) If true, renders the bottom cap.
 * - bDrawSides: (bool) If true, renders the cylindrical body.
 * - wireframe: (bool) If true, renders as wireframe.
 ******************************************/

void ShapeMeshes::DrawCylinderMesh(bool bDrawTop, bool bDrawBottom, bool bDrawSides, bool wireframe)
{
	// Set wireframe mode before binding VAO
	SetWireframeMode(wireframe);
	glBindVertexArray(m_CylinderMesh.vao);

	// **Calculate vertex counts**
	int bottomVertexCount = m_CylinderMesh.numSlices + 2;
	int topVertexCount = m_CylinderMesh.numSlices + 2;
	int sideVertexCount = (m_CylinderMesh.numSlices + 1) * 2;

	// **Draw bottom circle**
	if (bDrawBottom)
		glDrawElements(GL_TRIANGLES, m_CylinderMesh.numSlices * 3, GL_UNSIGNED_INT, 0);

	// **Draw top circle**
	if (bDrawTop)
		glDrawElements(GL_TRIANGLES, m_CylinderMesh.numSlices * 3, GL_UNSIGNED_INT, (void*)(m_CylinderMesh.numSlices * 3 * sizeof(GLuint)));

	// **Draw side faces**
	if (bDrawSides)
		glDrawElements(GL_TRIANGLES, m_CylinderMesh.numSlices * 6, GL_UNSIGNED_INT, (void*)((m_CylinderMesh.numSlices * 6) * sizeof(GLuint)));

	glBindVertexArray(0);
}


/******************************************
 * DrawPlaneMesh
 * ---------------------------------------
 * Binds the plane mesh's VAO and renders a
 * rectangular plane using indexed drawing.
 * Supports wireframe mode.
 *
 * Params:
 * - wireframe: (bool) If true, renders as wireframe.
 ******************************************/

void ShapeMeshes::DrawPlaneMesh(bool wireframe)
{
	// Set polygon mode before binding the VAO
	SetWireframeMode(wireframe);

	glBindVertexArray(m_PlaneMesh.vao);

	// Draw the plane using indexed drawing
	glDrawElements(GL_TRIANGLE_STRIP, m_PlaneMesh.nIndices, GL_UNSIGNED_INT, nullptr);

	glBindVertexArray(0);
}

/******************************************
 * DrawPrismMesh
 * ---------------------------------------
 * Binds the prism mesh's VAO and renders its base
 * and slanted faces using triangle strips.
 * Supports wireframe mode.
 *
 * Params:
 * - wireframe: (bool) If true, renders as wireframe.
 ******************************************/

void ShapeMeshes::DrawPrismMesh(bool wireframe)
{
	// Set polygon mode before binding the VAO
	SetWireframeMode(wireframe);

	glBindVertexArray(m_PrismMesh.vao);

	// Draw the base and slanted faces
	glDrawArrays(GL_TRIANGLE_STRIP, 0, m_PrismMesh.nVertices);

	glBindVertexArray(0); // Unbind the VAO after drawing
}

/******************************************
 * DrawPyramid3Mesh
 * ---------------------------------------
 * Binds the 3-sided pyramid mesh's VAO and renders
 * the triangular faces and the base using
 * triangle strips.
 * Supports wireframe mode.
 *
 * Params:
 * - wireframe: (bool) If true, renders as wireframe.
 ******************************************/

void ShapeMeshes::DrawPyramid3Mesh(bool wireframe)
{
	if (m_Pyramid3Mesh.nVertices == 0)
	{
		std::cerr << "Error: Pyramid mesh not loaded or empty!" << std::endl;
		return;
	}

	// Set polygon mode before binding the VAO
	SetWireframeMode(wireframe);

	glBindVertexArray(m_Pyramid3Mesh.vao);
	glDrawArrays(GL_TRIANGLE_STRIP, 0, m_Pyramid3Mesh.nVertices);
	glBindVertexArray(0); // Unbind the VAO after drawing
}

/******************************************
 * DrawPyramid4Mesh
 * ---------------------------------------
 * Binds the 4-sided pyramid mesh's VAO and renders
 * all four triangular faces along with the base.
 * Uses triangle strips for optimized rendering.
 * Supports wireframe mode.
 *
 * Params:
 * - wireframe: (bool) If true, renders as wireframe.
 ******************************************/

void ShapeMeshes::DrawPyramid4Mesh(bool wireframe)
{
	if (m_Pyramid4Mesh.nVertices == 0)
	{
		std::cerr << "Error: Pyramid mesh not loaded or has no vertices!" << std::endl;
		return;
	}

	// Set polygon mode before binding the VAO
	SetWireframeMode(wireframe);

	glBindVertexArray(m_Pyramid4Mesh.vao);
	glDrawArrays(GL_TRIANGLE_STRIP, 0, m_Pyramid4Mesh.nVertices);
	glBindVertexArray(0); // Unbind the VAO after drawing
}

/******************************************
 * DrawSphereMesh
 * ---------------------------------------
 * Binds the sphere mesh's VAO and renders a
 * complete sphere using indexed triangle drawing.
 * Supports wireframe mode.
 *
 * Params:
 * - wireframe: (bool) If true, renders as wireframe.
 ******************************************/

void ShapeMeshes::DrawSphereMesh(bool wireframe)
{
	SetWireframeMode(wireframe);
	glBindVertexArray(m_SphereMesh.vao);
	glDrawElements(GL_TRIANGLES, m_SphereMesh.nIndices, GL_UNSIGNED_INT, nullptr);
	glBindVertexArray(0);
}

void ShapeMeshes::DrawHemisphereMesh(bool wireframe)
{
	SetWireframeMode(wireframe);
	glBindVertexArray(m_HemisphereMesh.vao);
	glDrawElements(GL_TRIANGLES, m_HemisphereMesh.nIndices, GL_UNSIGNED_INT, nullptr);
	glBindVertexArray(0);
}


/******************************************
 * DrawHalfSphereMesh
 * ---------------------------------------
 * Binds the sphere mesh's VAO and renders the
 * top half of the sphere using indexed triangle
 * drawing.
 * Supports wireframe mode.
 *
 * Params:
 * - wireframe: (bool) If true, renders as wireframe.
 ******************************************/

void ShapeMeshes::DrawHalfSphereMesh(bool wireframe)
{
	if (m_SphereMesh.vao == 0 || m_SphereMesh.nIndices == 0)
	{
		std::cerr << "Error: Half-Sphere mesh VAO or indices not properly initialized." << std::endl;
		return;
	}

	// Set polygon mode before binding the VAO
	SetWireframeMode(wireframe);

	glBindVertexArray(m_SphereMesh.vao);
	glDrawElements(GL_TRIANGLES, m_SphereMesh.nIndices / 2, GL_UNSIGNED_INT, nullptr);
	glBindVertexArray(0); // Unbind the VAO after drawing
}

/******************************************
 * LoadFinMesh
 * ---------------------------------------
 * Generates a trapezoidal box mesh (fin) using
 * vertices and indices for rendering.
 *
 * - The fin is defined by its base length, top length,
 *   height, and thickness.
 * - Vertices are generated for the trapezoidal prism.
 * - Uses indexed drawing for efficient rendering.
 *
 * Params:
 * - baseLength: Length of the base of the trapezoid.
 * - topLength: Length of the top of the trapezoid.
 * - height: Height of the trapezoid.
 * - thickness: Thickness of the fin.
 ******************************************/

void ShapeMeshes::LoadFinMesh(float baseLength, float topLength, float height, float thickness)
{
	std::vector<GLfloat> nVertices;
	std::vector<GLuint> nIndices;

	float halfThickness = thickness / 2.0f;

	// Define Trapezoid with Right Angles
	glm::vec3 v0(0.0f, 0.0f, -halfThickness);      // Bottom-left (origin)
	glm::vec3 v1(baseLength, 0.0f, -halfThickness); // Bottom-right
	glm::vec3 v2(0.0f, height, -halfThickness);     // Top-left (aligned with bottom-left)
	glm::vec3 v3(topLength, height, -halfThickness); // Top-right

	glm::vec3 v4(0.0f, 0.0f, halfThickness);       // Bottom-left (back)
	glm::vec3 v5(baseLength, 0.0f, halfThickness);  // Bottom-right (back)
	glm::vec3 v6(0.0f, height, halfThickness);      // Top-left (back)
	glm::vec3 v7(topLength, height, halfThickness); // Top-right (back)

	// Function to add a vertex
	auto addVertex = [&](glm::vec3 v, glm::vec3 normal, glm::vec2 texCoord) {
		nVertices.insert(nVertices.end(), {
			v.x, v.y, v.z,
			normal.x, normal.y, normal.z,
			texCoord.x, texCoord.y
			});
	};

	// Front Face (Z-): Right-angled trapezoid with texture coordinates
	addVertex(v0, glm::vec3(0.0f, 0.0f, -1.0f), glm::vec2(0.0f, 0.0f)); // Bottom-left
	addVertex(v1, glm::vec3(0.0f, 0.0f, -1.0f), glm::vec2(1.0f, 0.0f)); // Bottom-right
	addVertex(v2, glm::vec3(0.0f, 0.0f, -1.0f), glm::vec2(0.0f, 1.0f)); // Top-left
	addVertex(v3, glm::vec3(0.0f, 0.0f, -1.0f), glm::vec2(1.0f, 1.0f)); // Top-right

	// Back Face (Z+): Mirrored trapezoid with texture coordinates
	addVertex(v4, glm::vec3(0.0f, 0.0f, 1.0f), glm::vec2(0.0f, 0.0f)); // Bottom-left (back)
	addVertex(v5, glm::vec3(0.0f, 0.0f, 1.0f), glm::vec2(1.0f, 0.0f)); // Bottom-right (back)
	addVertex(v6, glm::vec3(0.0f, 0.0f, 1.0f), glm::vec2(0.0f, 1.0f)); // Top-left (back)
	addVertex(v7, glm::vec3(0.0f, 0.0f, 1.0f), glm::vec2(1.0f, 1.0f)); // Top-right (back)

	// Top Face (Y+): No texture mapping for now
	addVertex(v2, glm::vec3(0.0f, 1.0f, 0.0f), glm::vec2(0.0f, 0.0f));
	addVertex(v3, glm::vec3(0.0f, 1.0f, 0.0f), glm::vec2(0.0f, 0.0f));
	addVertex(v6, glm::vec3(0.0f, 1.0f, 0.0f), glm::vec2(0.0f, 0.0f));
	addVertex(v7, glm::vec3(0.0f, 1.0f, 0.0f), glm::vec2(0.0f, 0.0f));

	// Bottom Face (Y-): No texture mapping for now
	addVertex(v0, glm::vec3(0.0f, -1.0f, 0.0f), glm::vec2(0.0f, 0.0f));
	addVertex(v1, glm::vec3(0.0f, -1.0f, 0.0f), glm::vec2(0.0f, 0.0f));
	addVertex(v4, glm::vec3(0.0f, -1.0f, 0.0f), glm::vec2(0.0f, 0.0f));
	addVertex(v5, glm::vec3(0.0f, -1.0f, 0.0f), glm::vec2(0.0f, 0.0f));

	// Left Face (X-): No texture mapping for now
	addVertex(v0, glm::vec3(-1.0f, 0.0f, 0.0f), glm::vec2(0.0f, 0.0f));
	addVertex(v2, glm::vec3(-1.0f, 0.0f, 0.0f), glm::vec2(0.0f, 0.0f));
	addVertex(v4, glm::vec3(-1.0f, 0.0f, 0.0f), glm::vec2(0.0f, 0.0f));
	addVertex(v6, glm::vec3(-1.0f, 0.0f, 0.0f), glm::vec2(0.0f, 0.0f));

	// Right Face (X+): No texture mapping for now
	addVertex(v1, glm::vec3(1.0f, 0.0f, 0.0f), glm::vec2(0.0f, 0.0f));
	addVertex(v3, glm::vec3(1.0f, 0.0f, 0.0f), glm::vec2(0.0f, 0.0f));
	addVertex(v5, glm::vec3(1.0f, 0.0f, 0.0f), glm::vec2(0.0f, 0.0f));
	addVertex(v7, glm::vec3(1.0f, 0.0f, 0.0f), glm::vec2(0.0f, 0.0f));


	// Define Index Order (Triangles)
	std::vector<GLuint> indices = {
		// Front Face (Trapezoid)
		0, 1, 2,  1, 3, 2,
		// Back Face (Trapezoid)
		4, 6, 5,  5, 6, 7,
		// Top Face
		8, 9, 10,  9, 11, 10,
		// Bottom Face
		12, 14, 13,  14, 15, 13,
		// Left Face
		16, 18, 17,  17, 18, 19,
		// Right Face
		20, 21, 22,  21, 23, 22
	};

	// Initialize VAO/VBO/EBO
	glGenVertexArrays(1, &m_FinMesh.vao);
	glGenBuffers(1, &m_FinMesh.vbo);
	glGenBuffers(1, &m_FinMesh.ebo);

	glBindVertexArray(m_FinMesh.vao);

	glBindBuffer(GL_ARRAY_BUFFER, m_FinMesh.vbo);
	glBufferData(GL_ARRAY_BUFFER, nVertices.size() * sizeof(GLfloat), nVertices.data(), GL_STATIC_DRAW);

	glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, m_FinMesh.ebo);
	glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices.size() * sizeof(GLuint), indices.data(), GL_STATIC_DRAW);

	glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 8 * sizeof(GLfloat), (void*)0); // Position
	glEnableVertexAttribArray(0);

	glVertexAttribPointer(1, 3, GL_FLOAT, GL_FALSE, 8 * sizeof(GLfloat), (void*)(3 * sizeof(GLfloat))); // Normal
	glEnableVertexAttribArray(1);

	glVertexAttribPointer(2, 2, GL_FLOAT, GL_FALSE, 8 * sizeof(GLfloat), (void*)(6 * sizeof(GLfloat))); // TexCoord
	glEnableVertexAttribArray(2);


	glBindVertexArray(0);

	// Store index count for drawing
	m_FinMesh.nIndices = static_cast<int>(indices.size());
}


/******************************************
 * DrawFinMesh
 * ---------------------------------------
 * Binds the fin mesh's VAO and renders it
 * using indexed drawing. Supports wireframe mode.
 *
 * Params:
 * - wireframe: (bool) If true, renders as wireframe.
 ******************************************/

void ShapeMeshes::DrawFinMesh(bool wireframe)
{
	glPolygonMode(GL_FRONT_AND_BACK, wireframe ? GL_LINE : GL_FILL);

	glBindVertexArray(m_FinMesh.vao);
	glDrawElements(GL_TRIANGLES, m_FinMesh.nIndices, GL_UNSIGNED_INT, 0);
	glBindVertexArray(0);

	glPolygonMode(GL_FRONT_AND_BACK, GL_FILL); // Reset mode
}

void ShapeMeshes::DrawFinSides()
{
	glBindVertexArray(m_FinMesh.vao);

	// Front Face (first 6 indices)
	glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);

	// Back Face (next 6 indices)
	glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, (void*)(6 * sizeof(GLuint)));

	glBindVertexArray(0);
}

void ShapeMeshes::DrawFinFrontOnly()
{
	glBindVertexArray(m_FinMesh.vao);
	glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0); // Front face = first 6 indices
	glBindVertexArray(0);
}

void ShapeMeshes::DrawFinBackOnly()
{
	glBindVertexArray(m_FinMesh.vao);
	glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, (void*)(6 * sizeof(GLuint))); // Back face
	glBindVertexArray(0);
}

/******************************************
 * DrawFinUntexturedSides
 * ---------------------------------------
 * Draws only the top, bottom, left, and right
 * faces of the fin, skipping the front and back.
 ******************************************/
void ShapeMeshes::DrawFinUntexturedSides()
{
	glBindVertexArray(m_FinMesh.vao);

	// Skip first 12 indices (front and back)
	// Top, Bottom, Left, Right = 6 indices each × 4 faces = 24 indices
	glDrawElements(GL_TRIANGLES, 24, GL_UNSIGNED_INT, (void*)(12 * sizeof(GLuint)));

	glBindVertexArray(0);
}

/******************************************
 * DrawTaperedCylinderMesh
 * ---------------------------------------
 * Binds the tapered cylinder mesh's VAO and renders
 * the top cap, bottom cap, and sides based on the
 * given parameters.
 * Supports wireframe mode.
 *
 * Params:
 * - bDrawTop: (bool) If true, renders the top cap.
 * - bDrawBottom: (bool) If true, renders the bottom cap.
 * - bDrawSides: (bool) If true, renders the tapered sides.
 * - wireframe: (bool) If true, renders as wireframe.
 ******************************************/
void ShapeMeshes::DrawTaperedCylinderMesh(bool bDrawTop, bool bDrawBottom, bool bDrawSides, bool wireframe)
{
	SetWireframeMode(wireframe);
	glBindVertexArray(m_TaperedCylinderMesh.vao);

	const int n = m_TaperedCylinderMesh.numSlices;

	const GLsizei bottomCount = n * 3; // numSlices triangles * 3 indices
	const GLsizei topCount = n * 3;
	const GLsizei sideCount = n * 6;

	const size_t bottomOff = 0;
	const size_t topOff = bottomCount;
	const size_t sideOff = bottomCount + topCount;

	if (bDrawBottom)
		glDrawElements(GL_TRIANGLES, bottomCount, GL_UNSIGNED_INT, (void*)(sizeof(GLuint) * bottomOff));

	if (bDrawTop)
		glDrawElements(GL_TRIANGLES, topCount, GL_UNSIGNED_INT, (void*)(sizeof(GLuint) * topOff));

	if (bDrawSides)
		glDrawElements(GL_TRIANGLES, sideCount, GL_UNSIGNED_INT, (void*)(sizeof(GLuint) * sideOff));

	glBindVertexArray(0);
}


/******************************************
 * DrawTorusMesh
 * ---------------------------------------
 * Binds the torus mesh's VAO and renders the
 * full torus using indexed triangle drawing.
 * Supports wireframe mode.
 *
 * Params:
 * - wireframe: (bool) If true, renders as wireframe.
 ******************************************/

void ShapeMeshes::DrawTorusMesh(bool wireframe)
{
	if (m_TorusMesh.vao == 0 || m_TorusMesh.nIndices == 0)
	{
		std::cerr << "Error: Torus mesh VAO or indices not properly initialized." << std::endl;
		return;
	}

	// Set polygon mode before binding the VAO
	SetWireframeMode(wireframe);

	glBindVertexArray(m_TorusMesh.vao);
	glDrawElements(GL_TRIANGLES, m_TorusMesh.nIndices, GL_UNSIGNED_INT, nullptr);
	glBindVertexArray(0);
}

///////////////////////////////////////////////////
//	DrawExtraTorusMesh1()
//
//	Transform and draw the torus mesh to the window.
// 
///////////////////////////////////////////////////
void ShapeMeshes::DrawExtraTorusMesh1()
{
	glBindVertexArray(m_ExtraTorusMesh1.vao);

	glDrawArrays(GL_TRIANGLES, 0, m_ExtraTorusMesh1.nVertices);

	glBindVertexArray(0);
}

///////////////////////////////////////////////////
//	DrawExtraTorusMesh2()
//
//	Transform and draw the torus mesh to the window.
// 
///////////////////////////////////////////////////
void ShapeMeshes::DrawExtraTorusMesh2()
{
	glBindVertexArray(m_ExtraTorusMesh2.vao);

	glDrawArrays(GL_TRIANGLES, 0, m_ExtraTorusMesh2.nVertices);

	glBindVertexArray(0);
}
/******************************************
 * DrawHalfTorusMesh
 * ---------------------------------------
 * Binds the torus mesh's VAO and renders only
 * the upper half of the torus using indexed
 * triangle drawing.
 * Supports wireframe mode.
 *
 * Params:
 * - wireframe: (bool) If true, renders as wireframe.
 ******************************************/

void ShapeMeshes::DrawHalfTorusMesh(bool wireframe)
{
	if (m_TorusMesh.vao == 0 || m_TorusMesh.nIndices == 0)
	{
		std::cerr << "Error: Torus mesh VAO or indices not properly initialized." << std::endl;
		return;
	}

	// Set polygon mode before binding the VAO
	SetWireframeMode(wireframe);

	glBindVertexArray(m_TorusMesh.vao);
	glDrawElements(GL_TRIANGLES, m_TorusMesh.nIndices / 2, GL_UNSIGNED_INT, nullptr);
	glBindVertexArray(0);
}

/******************************************
 * DrawSpringMesh
 * ---------------------------------------
 * Binds and renders the 3D helical spring.
 * Supports wireframe mode.
 *
 * Params:
 * - wireframe: (bool) If true, renders in wireframe mode.
 ******************************************/
void ShapeMeshes::DrawSpringMesh(bool wireframe)
{
	if (m_SpringMesh.vao == 0 || m_SpringMesh.nVertices == 0)
	{
		std::cerr << "Error: Spring mesh not initialized properly." << std::endl;
		return;
	}

	SetWireframeMode(wireframe);

	glBindVertexArray(m_SpringMesh.vao);
	glDrawElements(GL_TRIANGLES, m_SpringMesh.nIndices, GL_UNSIGNED_INT, nullptr);
	glBindVertexArray(0);
}

/******************************************
 * DrawTubeMesh
 * ---------------------------------------
 * Binds the tube mesh’s VAO and renders it
 * using indexed drawing. Supports wireframe mode.
 *
 * Params:
 * - wireframe: (bool) If true, renders as wireframe.
 ******************************************/
void ShapeMeshes::DrawTubeMesh(bool wireframe) const
{
	if (m_TubeMesh.vao == 0 || m_TubeMesh.nIndices == 0) {
		std::cerr << "Error: Tube mesh not initialized properly." << std::endl;
		return;
	}

	SetWireframeMode(wireframe);

	glBindVertexArray(m_TubeMesh.vao);
	glDrawElements(GL_TRIANGLES, m_TubeMesh.nIndices, GL_UNSIGNED_INT, nullptr);
	glBindVertexArray(0);
}

/******************************************
* Deprecated Functions
* ****************************************/

void ShapeMeshes::DrawBoxMeshLines() {
	if (!boxWarned) {
		std::cerr << "Warning: DrawBoxMeshLines() is deprecated. Use DrawBoxMesh(true) instead.\n";
		boxWarned = true;
	}
	DrawBoxMesh(true);
}
/*
void ShapeMeshes::DrawHalfSphereMesh() {
	if (!boxWarned) {
		std::cerr << "Warning: DrawHalfSphereMesh() is deprecated. Use DrawHemisphereMesh(true) instead.\n";
		boxWarned = true;
	}
	DrawHemisphereMesh(true);
}
*/
void ShapeMeshes::DrawConeMeshLines() {
	if (!coneWarned) {
		std::cerr << "Warning: DrawConeMeshLines() is deprecated. Use DrawConeMesh(true) instead.\n";
		coneWarned = true;
	}
	DrawConeMesh(true);
}

void ShapeMeshes::DrawCylinderMeshLines() {
	if (!cylinderWarned) {
		std::cerr << "Warning: DrawCylinderMeshLines() is deprecated. Use DrawCylinderMesh(true) instead.\n";
		cylinderWarned = true;
	}
	DrawCylinderMesh(true);
}

void ShapeMeshes::DrawPlaneMeshLines() {
	if (!planeWarned) {
		std::cerr << "Warning: DrawPlaneMeshLines() is deprecated. Use DrawPlaneMesh(true) instead.\n";
		planeWarned = true;
	}
	DrawPlaneMesh(true);
}

void ShapeMeshes::DrawPrismMeshLines() {
	if (!prismWarned) {
		std::cerr << "Warning: DrawPrismMeshLines() is deprecated. Use DrawPrismMesh(true) instead.\n";
		prismWarned = true;
	}
	DrawPrismMesh(true);
}

void ShapeMeshes::DrawPyramid3MeshLines() {
	if (!pyramid3Warned) {
		std::cerr << "Warning: DrawPyramid3MeshLines() is deprecated. Use DrawPyramid3Mesh(true) instead.\n";
		pyramid3Warned = true;
	}
	DrawPyramid3Mesh(true);
}

void ShapeMeshes::DrawPyramid4MeshLines() {
	if (!pyramid4Warned) {
		std::cerr << "Warning: DrawPyramid4MeshLines() is deprecated. Use DrawPyramid4Mesh(true) instead.\n";
		pyramid4Warned = true;
	}
	DrawPyramid4Mesh(true);
}

void ShapeMeshes::DrawSphereMeshLines() {
	if (!sphereWarned) {
		std::cerr << "Warning: DrawSphereMeshLines() is deprecated. Use DrawSphereMesh(true) instead.\n";
		sphereWarned = true;
	}
	DrawSphereMesh(true);
}

void ShapeMeshes::DrawHalfSphereMeshLines() {
	if (!halfSphereWarned) {
		std::cerr << "Warning: DrawHalfSphereMeshLines() is deprecated. Use DrawHalfSphereMesh(true) instead.\n";
		halfSphereWarned = true;
	}
	DrawHemisphereMesh(true);
}

void ShapeMeshes::DrawTaperedCylinderMeshLines() {
	if (!taperedCylinderWarned) {
		std::cerr << "Warning: DrawTaperedCylinderMeshLines() is deprecated. Use DrawTaperedCylinderMesh(true) instead.\n";
		taperedCylinderWarned = true;
	}
	DrawTaperedCylinderMesh(true);
}

void ShapeMeshes::DrawTorusMeshLines() {
	if (!torusWarned) {
		std::cerr << "Warning: DrawTorusMeshLines() is deprecated. Use DrawTorusMesh(true) instead.\n";
		torusWarned = true;
	}
	DrawTorusMesh(true);
}

void ShapeMeshes::DrawHalfTorusMeshLines() {
	if (!halfTorusWarned) {
		std::cerr << "Warning: DrawHalfTorusMeshLines() is deprecated. Use DrawHalfTorusMesh(true) instead.\n";
		halfTorusWarned = true;
	}
	DrawHalfTorusMesh(true);
}

/******************************************
 * QuadCrossProduct
 * ---------------------------------------
 * Computes the normal vector for a quadrilateral
 * using two adjacent triangle faces.
 *
 * @param p1 First vertex of the quad.
 * @param p2 Second vertex of the quad.
 * @param p3 Third vertex of the quad.
 * @param p4 Fourth vertex of the quad.
 * @return Normalized normal vector for the quad.
 ******************************************/

glm::vec3 ShapeMeshes::QuadCrossProduct(const glm::vec3& p1, const glm::vec3& p2, const glm::vec3& p3, const glm::vec3& p4)
{
	glm::vec3 n1 = glm::cross(p2 - p1, p3 - p1);
	glm::vec3 n2 = glm::cross(p4 - p1, p3 - p1);
	return glm::normalize(n1 + n2); // Average the two normals
}

/******************************************
 * CalculateTriangleNormal
 * ---------------------------------------
 * Computes the normal vector for a single triangle.
 *
 * @param p1 First vertex of the triangle.
 * @param p2 Second vertex of the triangle.
 * @param p3 Third vertex of the triangle.
 * @return Normalized normal vector for the triangle.
 ******************************************/

glm::vec3 ShapeMeshes::CalculateTriangleNormal(const glm::vec3& p1, const glm::vec3& p2, const glm::vec3& p3)
{
	return glm::normalize(glm::cross(p2 - p1, p3 - p1));
}

/******************************************
 * SetShaderMemoryLayout
 * ---------------------------------------
 * Configures the vertex attributes for the shader
 * memory layout.
 *
 * - Attribute 0: Position (vec3)
 * - Attribute 1: Normal (vec3)
 * - Attribute 2: Texture Coordinates (vec2)
 ******************************************/

void ShapeMeshes::SetShaderMemoryLayout()
{
	constexpr GLsizei stride = sizeof(Vertex);

	glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, stride, (void*)offsetof(Vertex, position));
	glEnableVertexAttribArray(0);

	glVertexAttribPointer(1, 3, GL_FLOAT, GL_FALSE, stride, (void*)offsetof(Vertex, normal));
	glEnableVertexAttribArray(1);

	glVertexAttribPointer(2, 2, GL_FLOAT, GL_FALSE, stride, (void*)offsetof(Vertex, texCoord)); // Consistent naming
	glEnableVertexAttribArray(2);
}

/************************************************************************************
 * Custom Parametric Meshes
 * ---------------------------------------
 * Author: Jennifer Lakey
 * Course: CS 330 - Computational Graphics and Visualization
 * Module: ShapeMeshes.cpp (Custom Additions)
 *
 * Overview:
 * These meshes extend the rendering engine with
 * original, procedurally generated 3D geometry.
 * Each shape is built from mathematical models
 * and computed entirely at runtime using nested
 * parametric loops, analytic normals, and
 * interleaved vertex buffers.
 *
 * Implemented Custom Shapes:
 *
 * 1. Curved Cone
 *    - A cone whose centerline follows a circular
 *      arc. The mesh is generated by sweeping a
 *      shrinking radius along a curved path.
 *    - Uses tangent and normal directions derived
 *      from the arc to orient each ring of vertices.
 *
 * 2. Tapered Torus
 *    - A torus whose tube radius varies along the
 *      sweep angle. This produces a "thick-to-thin"
 *      toroidal shape.
 *    - Geometry is computed from two nested angles:
 *      the main rotation and the tube rotation.
 *
 * 3. Spiral Mesh
 *    - A helical tube that expands outward as it
 *      rotates. The centerline is a spiral curve,
 *      and each ring uses a Frenet-like frame to
 *      maintain consistent orientation.
 *    - Includes a hemispherical cap generated from
 *      spherical coordinates.
 *
 * 4. Sine-Deformed Cone
 *    - A cone whose profile is modulated by a sine
 *      wave. The deformation is applied along the
 *      height, producing a rippled surface.
 *    - Normals are accumulated from face normals
 *      for smooth shading.
 *
 * 5. Superellipsoid (New Enhancement)
 *    - A generalized ellipsoid defined by two
 *      exponents controlling horizontal and vertical
 *      "squareness." This shape demonstrates advanced
 *      parametric modeling and algorithmic optimization.
 *    - Uses analytic normals derived from the implicit
 *      superquadric formulation.
 *
 * Notes:
 * - All custom meshes use interleaved vertex data
 *   (position, normal, UV) and follow the same
 *   shader memory layout as the instructor-provided
 *   primitives.
 * - These shapes are designed to be modular,
 *   mathematically transparent, and efficient.
 ************************************************************************************/

 /******************************************
 * Curved Cone Mesh
 * ---------------------------------------
 * Author: Jennifer Lakey
 *
 * This function procedurally generates a cone
 * whose centerline follows a circular arc.
 *
 * The cone is divided into:
 *   - curveSteps: number of steps along the arc
 *   - numSlices:  number of radial slices per ring
 *
 * For each step along the arc:
 *   1. Compute the center point on the curved path.
 *   2. Compute a tangent vector along the arc.
 *   3. Derive a perpendicular "normal direction"
 *      to orient the circular cross-section.
 *   4. Shrink the cone radius linearly from base
 *      to tip.
 *   5. Sweep a circle around the local frame to
 *      generate vertices.
 *
 * Normals are approximated by normalizing the
 * offset from the centerline. UVs map slices to U
 * and arc progression to V.
 ******************************************/

void ShapeMeshes::LoadCurvedConeMesh(int numSlices, int curveSteps, float radius, float height, float bendRadius)
{
	// Ensure minimum valid geometry
	if (numSlices < 3) numSlices = 3;
	if (curveSteps < 1) curveSteps = 1;

	// Store parameters in mesh struct
	m_CurvedConeMesh.numSlices = numSlices;
	m_CurvedConeMesh.curveSteps = curveSteps;

	// Interleaved vertex buffer and index buffer
	std::vector<GLfloat> verts;
	std::vector<GLuint> indices;

	// Angle between radial slices
	float angleStep = 2.0f * Pi / static_cast<float>(numSlices);
	// Total bend angle of the arc (height mapped onto circular arc)
	float bendAngle = height / bendRadius;

	// Generate rings along the curved centerline
	for (int step = 0; step <= curveSteps; ++step) {
		float t = static_cast<float>(step) / curveSteps;  // normalized arc parameter
		float arcTheta = t * bendAngle;  // angle along the bend

		// Compute center point on the circular arc
		float centerX = bendRadius * sin(arcTheta);
		float centerY = bendRadius * (1.0f - cos(arcTheta));
		float centerZ = 0.0f;

		// Tangent direction along the arc
		glm::vec3 tangent(cos(arcTheta), sin(arcTheta), 0.0f);
		// Perpendicular direction used to orient the cone's circular cross-section
		glm::vec3 normalDir = glm::normalize(glm::vec3(-tangent.y, tangent.x, 0.0f));

		// Linearly shrinking radius from base to tip
		float coneRadius = radius * (1.0f - t);

		// Sweep a circle around the local frame
		for (int slice = 0; slice <= numSlices; ++slice) {
			float angle = slice * angleStep;
			// Local circle coordinates
			float localX = coneRadius * cos(angle);
			float localZ = coneRadius * sin(angle);

			// Offset from centerline using local frame
			glm::vec3 offset = normalDir * localX + glm::vec3(0.0f, 0.0f, localZ);
			// Final vertex position
			glm::vec3 position = glm::vec3(centerX, centerY, centerZ) + offset;

			// Approximate normal: direction away from centerline
			glm::vec3 normal = glm::normalize(offset);

			// UV coordinates: slice index -> U, arc progression -> V
			float u = static_cast<float>(slice) / numSlices;
			float v = t;

			// Pack vertex: position (3), normal (3), UV (2)
			verts.push_back(position.x);
			verts.push_back(position.y);
			verts.push_back(position.z);
			verts.push_back(normal.x);
			verts.push_back(normal.y);
			verts.push_back(normal.z);
			verts.push_back(u);
			verts.push_back(v);
		}
	}

	// Build triangle indices between adjacent rings
	for (int step = 0; step < curveSteps; ++step) {
		for (int slice = 0; slice < numSlices; ++slice) {
			int current = step * (numSlices + 1) + slice;
			int next = (step + 1) * (numSlices + 1) + slice;

			// First triangle
			indices.push_back(current);
			indices.push_back(next);
			indices.push_back(current + 1);

			// Second triangle
			indices.push_back(current + 1);
			indices.push_back(next);
			indices.push_back(next + 1);
		}
	}

	// Upload mesh to GPU using centralized initializer
	InitializeMesh(m_CurvedConeMesh, verts, indices);
}


/******************************************
 * DrawCurvedConeMesh
 * ---------------------------------------
 * Binds the VAO and renders the curved cone
 * using indexed triangle drawing.
 ******************************************/
void ShapeMeshes::DrawCurvedConeMesh()
{
	glBindVertexArray(m_CurvedConeMesh.vao);
	glDrawElements(GL_TRIANGLES, m_CurvedConeMesh.nIndices, GL_UNSIGNED_INT, nullptr);
	glBindVertexArray(0);
}


/******************************************
 * Tapered Torus Mesh
 * ---------------------------------------
 * Author: Jennifer Lakey
 *
 * This function renders a torus whose tube
 * radius varies smoothly along the sweep
 * angle. The result is a "tapered" torus
 * that transitions from tubeRadiusStart to
 * tubeRadiusEnd.
 *
 * Geometry is generated using two nested
 * angular parameters:
 *
 *   - theta: rotation around the main ring
 *   - phi:   rotation around the tube
 *
 * For each main segment:
 *   1. Compute the center point on the ring.
 *   2. Linearly interpolate the tube radius.
 *   3. Sweep a circle around the ring using phi.
 *
 * Normals are computed analytically from the
 * parametric torus formulation and normalized.
 *
 * The mesh is uploaded dynamically in the draw
 * function to allow the same mesh object to be
 * reused with different parameter values during
 * a single scene render.
 *
 * Prior to this design, parameters had to be
 * supplied in the load function, which meant
 * each parameter variation required loading a
 * separate copy of the mesh. By moving the
 * parameter-dependent geometry generation into
 * the draw function, a single VAO/VBO/EBO setup
 * can support multiple distinct shapes without
 * reallocation.
 ******************************************/

void ShapeMeshes::LoadTaperedTorusMesh()
{
	// Allocate VAO, VBO, and EBO once.
	// Vertex/index data will be uploaded in DrawTaperedTorusMesh().
	glGenVertexArrays(1, &m_TaperedTorusMesh.vao);
	glGenBuffers(1, &m_TaperedTorusMesh.vbo);
	glGenBuffers(1, &m_TaperedTorusMesh.ebo);
}

void ShapeMeshes::DrawTaperedTorusMesh(
	float mainRadius,
	float tubeRadiusStart,
	float tubeRadiusEnd,
	int mainSegments,
	int tubeSegments,
	float sweepAngleRadians)
{
	// Interleaved vertex buffer and index buffer
	std::vector<GLfloat> verts;
	std::vector<GLuint> indices;

	// Angular increments for main ring and tube
	float mainStep = sweepAngleRadians / mainSegments;
	float tubeStep = 2.0f * Pi / tubeSegments;

	// Generate vertices along the main ring
	for (int i = 0; i <= mainSegments; ++i) {
		float theta = i * mainStep;							// angle around main ring
		float sweepT = static_cast<float>(i) / mainSegments;		// normalized sweep
		float tubeRadius = glm::mix(tubeRadiusStart, tubeRadiusEnd, sweepT);    // tapered radius

		// Center of tube cross-section on the main ring
		glm::vec3 center = glm::vec3(mainRadius * cos(theta), mainRadius * sin(theta), 0.0f);

		// Sweep tube around the ring
		for (int j = 0; j <= tubeSegments; ++j) {
			float phi = j * tubeStep;

			// Parametric torus normal direction
			glm::vec3 normal = glm::vec3(cos(phi) * cos(theta), cos(phi) * sin(theta), sin(phi));
			// Final vertex position = center + normal * tubeRadius
			glm::vec3 position = center + normal * tubeRadius;
			// Normalize the analytic normal
			glm::vec3 normalized = glm::normalize(normal);

			// UV coordinates: tube sweep -> U, main sweep -> V
			float u = static_cast<float>(j) / tubeSegments;
			float v = sweepT;

			// Pack interleaved vertex: position (3), normal (3), UV (2)
			verts.push_back(position.x);
			verts.push_back(position.y);
			verts.push_back(position.z);
			verts.push_back(normalized.x);
			verts.push_back(normalized.y);
			verts.push_back(normalized.z);
			verts.push_back(u);
			verts.push_back(v);
		}
	}

	// Build triangle indices between adjacent rings
	for (int i = 0; i < mainSegments; ++i) {
		for (int j = 0; j < tubeSegments; ++j) {
			int curr = i * (tubeSegments + 1) + j;
			int next = (i + 1) * (tubeSegments + 1) + j;

			// First triangle
			indices.push_back(curr);
			indices.push_back(next);
			indices.push_back(curr + 1);

			// Second triangle
			indices.push_back(curr + 1);
			indices.push_back(next);
			indices.push_back(next + 1);
		}
	}

	// Upload to GPU (dynamic draw pattern)
	glBindVertexArray(m_TaperedTorusMesh.vao);

	glBindBuffer(GL_ARRAY_BUFFER, m_TaperedTorusMesh.vbo);
	glBufferData(GL_ARRAY_BUFFER, verts.size() * sizeof(GLfloat), verts.data(), GL_DYNAMIC_DRAW);

	// Vertex attribute layout: position (0), normal (1), UV (2)
	glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 8 * sizeof(GLfloat), (void*)0); // position
	glEnableVertexAttribArray(0);
	glVertexAttribPointer(1, 3, GL_FLOAT, GL_FALSE, 8 * sizeof(GLfloat), (void*)(3 * sizeof(GLfloat))); // normal
	glEnableVertexAttribArray(1);
	glVertexAttribPointer(2, 2, GL_FLOAT, GL_FALSE, 8 * sizeof(GLfloat), (void*)(6 * sizeof(GLfloat))); // UV
	glEnableVertexAttribArray(2);

	glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, m_TaperedTorusMesh.ebo);
	glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices.size() * sizeof(GLuint), indices.data(), GL_DYNAMIC_DRAW);

	// Draw the tapered torus
	glDrawElements(GL_TRIANGLES, static_cast<GLsizei>(indices.size()), GL_UNSIGNED_INT, nullptr);
	glBindVertexArray(0);
}

/******************************************
 * Spiral Mesh
 * ---------------------------------------
 * Author: Jennifer Lakey
 *
 * This function generates a helical tube whose
 * centerline expands outward as it rotates.
 *
 * The spiral is defined by:
 *   - tubeRadius:     radius of the tube itself
 *   - flattenFactor:  flattens the tube along X
 *   - loopSpacing:    radial growth per revolution
 *   - numLoops:       number of spiral turns
 *   - tubeSegments:   number of segments around tube
 *   - spiralSegments: number of segments along spiral
 *
 * The centerline is computed first, producing a
 * sequence of points along an expanding spiral.
 * Tangent vectors are derived from these points.
 *
 * A local coordinate frame (tangent, normal,
 * binormal) is constructed for each ring using a
 * Frenet-like method. This ensures the tube does
 * not twist unpredictably as it follows the curve.
 *
 * A hemispherical cap is generated at the start
 * of the spiral to close the tube cleanly.
 *
 * The mesh is uploaded dynamically in the draw
 * function to allow the same mesh object to be
 * reused with different parameter values during
 * a single scene render. Prior to this design,
 * parameters had to be supplied in the load
 * function, which meant each parameter variation
 * required loading a separate copy of the mesh.
 ******************************************/

void ShapeMeshes::LoadSpiralMesh() {
	// Allocate VAO/VBO/EBO once; geometry is uploaded in DrawSpiralMesh().
	glGenVertexArrays(1, &m_SpiralMesh.vao);
	glGenBuffers(1, &m_SpiralMesh.vbo);
	glGenBuffers(1, &m_SpiralMesh.ebo);
}

void ShapeMeshes::DrawSpiralMesh(
	float tubeRadius,
	float flattenFactor,
	float loopSpacing,
	float numLoops,
	int tubeSegments,
	int spiralSegments
) {
	std::vector<GLfloat> verts;
	std::vector<GLuint> indices;

	const float PI = 3.14159265f;
	// Total angular sweep of the spiral
	float totalAngle = numLoops * 2.0f * PI;
	// Step size along spiral and around tube
	float spiralStep = totalAngle / spiralSegments;
	float tubeStep = 2.0f * PI / tubeSegments;

	// Start halfway around the circle to create a partial-loop effect
	float startAngle = PI;
	int startSegment = static_cast<int>(startAngle / spiralStep);

	// Up direction for flattening and frame construction
	glm::vec3 worldUp(1.0f, 0.0f, 0.0f); // Flatten along X axis

	std::vector<glm::vec3> centers;
	std::vector<glm::vec3> tangents;

	// --- Generate spiral centerline with partial loop support ---
	for (int i = startSegment; i <= spiralSegments; ++i) {
		float theta = i * spiralStep;
		if (theta > totalAngle) break;

		// Spiral radius increases with angle
		float radius = loopSpacing * theta / (2.0f * PI);
		// Center point on spiral
		centers.push_back(glm::vec3(radius * cos(theta), radius * sin(theta), 0.0f));
	}

	int ringCount = static_cast<int>(centers.size());

	// --- Compute tangent vectors along the centerline ---
	for (int i = 0; i < ringCount; ++i) {
		glm::vec3 tangent;
		if (i == 0) {
			tangent = glm::normalize(centers[1] - centers[0]);
		}
		else if (i == ringCount - 1) {
			tangent = glm::normalize(centers[ringCount - 1] - centers[ringCount - 2]);
		}
		else {
			tangent = glm::normalize(centers[i + 1] - centers[i - 1]);
		}
		tangents.push_back(tangent);
	}

	int ringStride = tubeSegments;
	glm::vec3 prevNormal, prevBinormal;
	std::vector<GLuint> ringStartIndices;

	// --- Generate tube rings along the spiral ---
	for (int i = 0; i < ringCount; ++i) {
		float sweepT = static_cast<float>(i) / (ringCount - 1);
		glm::vec3 center = centers[i];
		glm::vec3 tangent = tangents[i];

		glm::vec3 normal, binormal;
		// Construct local frame
		if (i == 0) {
			// Initial frame from worldUp
			binormal = glm::normalize(glm::cross(tangent, worldUp));
			normal = glm::normalize(glm::cross(binormal, tangent));
		}
		else {
			// Rotate previous frame to align with new tangent
			glm::vec3 v = tangents[i - 1];
			glm::vec3 w = tangent;
			glm::vec3 axis = glm::normalize(glm::cross(v, w));
			float angle = acos(glm::clamp(glm::dot(v, w), -1.0f, 1.0f));
			glm::mat3 rot = glm::mat3(glm::rotate(glm::mat4(1.0f), angle, axis));
			normal = glm::normalize(rot * prevNormal);
			binormal = glm::normalize(glm::cross(tangent, normal));
		}

		prevNormal = normal;
		prevBinormal = binormal;

		// Sweep tube around the ring
		for (int j = 0; j < tubeSegments; ++j) {
			float phi = j * tubeStep;
			float x = cos(phi);
			float y = sin(phi);

			// Offset from center using local frame
			glm::vec3 offset = x * normal * (1.0f - flattenFactor) + y * binormal;
			glm::vec3 position = center + offset * tubeRadius;
			glm::vec3 normalVec = glm::normalize(offset);

			float u = static_cast<float>(j) / tubeSegments;
			float v = sweepT;

			// Pack vertex: position (3), normal (3), UV (2)
			verts.push_back(position.x);
			verts.push_back(position.y);
			verts.push_back(position.z);
			verts.push_back(normalVec.x);
			verts.push_back(normalVec.y);
			verts.push_back(normalVec.z);
			verts.push_back(u);
			verts.push_back(v);
		}

		// Store first ring indices for cap stitching
		if (i == 0) {
			for (int j = 0; j < tubeSegments; ++j) {
				ringStartIndices.push_back(j);
			}
		}
	}

	// --- Connect tube rings with triangles ---
	for (int i = 0; i < ringCount - 1; ++i) {
		for (int j = 0; j < tubeSegments; ++j) {
			int curr = i * ringStride + j;
			int next = (i + 1) * ringStride + j;
			int currNext = i * ringStride + (j + 1) % tubeSegments;
			int nextNext = (i + 1) * ringStride + (j + 1) % tubeSegments;

			indices.push_back(curr);
			indices.push_back(next);
			indices.push_back(currNext);

			indices.push_back(currNext);
			indices.push_back(next);
			indices.push_back(nextNext);
		}
	}

	// --- Hemisphere cap at start of spiral ---
	glm::vec3 capCenter = centers[0];
	glm::vec3 capTangent = tangents[0];
	glm::vec3 capBinormal = glm::normalize(glm::cross(capTangent, worldUp));
	glm::vec3 capNormal = glm::normalize(glm::cross(capBinormal, capTangent));

	int capRings = 8;
	int capSegments = tubeSegments;
	int baseIndex = static_cast<int>(verts.size() / 8);

	for (int i = 1; i <= capRings; ++i) {
		float theta = (i * PI * 0.5f) / capRings;
		float r = sin(theta);
		float z = cos(theta);

		for (int j = 0; j < capSegments; ++j) {
			float phi = j * tubeStep;
			float x = cos(phi);
			float y = sin(phi);

			glm::vec3 radial = x * capNormal * (1.0f - flattenFactor) + y * capBinormal;
			glm::vec3 offset = radial * r * tubeRadius + capTangent * z * tubeRadius;
			glm::vec3 position = capCenter - offset;
			glm::vec3 normalVec = glm::normalize(-offset);

			float u = static_cast<float>(j) / capSegments;
			float v = -z;

			verts.push_back(position.x);
			verts.push_back(position.y);
			verts.push_back(position.z);
			verts.push_back(normalVec.x);
			verts.push_back(normalVec.y);
			verts.push_back(normalVec.z);
			verts.push_back(u);
			verts.push_back(v);
		}
	}

	// --- Stitch hemisphere rings together ---
	for (int i = 0; i < capRings - 1; ++i) {
		for (int j = 0; j < capSegments; ++j) {
			int curr = baseIndex + i * capSegments + j;
			int next = baseIndex + (i + 1) * capSegments + j;
			int currNext = baseIndex + i * capSegments + (j + 1) % capSegments;
			int nextNext = baseIndex + (i + 1) * capSegments + (j + 1) % capSegments;

			indices.push_back(curr);
			indices.push_back(next);
			indices.push_back(currNext);

			indices.push_back(currNext);
			indices.push_back(next);
			indices.push_back(nextNext);
		}
	}

	// --- Connect hemisphere to first tube ring ---
	for (int j = 0; j < capSegments; ++j) {
		int capRing = baseIndex + (capRings - 1) * capSegments + j;
		int tubeRing = ringStartIndices[j];
		int capNext = baseIndex + (capRings - 1) * capSegments + (j + 1) % capSegments;
		int tubeNext = ringStartIndices[(j + 1) % capSegments];

		indices.push_back(capRing);
		indices.push_back(tubeRing);
		indices.push_back(capNext);

		indices.push_back(capNext);
		indices.push_back(tubeRing);
		indices.push_back(tubeNext);
	}

	// --- Upload mesh to GPU ---
	glBindVertexArray(m_SpiralMesh.vao);
	glBindBuffer(GL_ARRAY_BUFFER, m_SpiralMesh.vbo);
	glBufferData(GL_ARRAY_BUFFER, verts.size() * sizeof(GLfloat), verts.data(), GL_DYNAMIC_DRAW);

	// Vertex attribute layout: position (0), normal (1), UV (2)
	glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 8 * sizeof(GLfloat), (void*)0);
	glEnableVertexAttribArray(0);
	glVertexAttribPointer(1, 3, GL_FLOAT, GL_FALSE, 8 * sizeof(GLfloat), (void*)(3 * sizeof(GLfloat)));
	glEnableVertexAttribArray(1);
	glVertexAttribPointer(2, 2, GL_FLOAT, GL_FALSE, 8 * sizeof(GLfloat), (void*)(6 * sizeof(GLfloat)));
	glEnableVertexAttribArray(2);

	glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, m_SpiralMesh.ebo);
	glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, m_SpiralMesh.ebo);
	glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices.size() * sizeof(GLuint), indices.data(), GL_DYNAMIC_DRAW);

	// Draw the spiral mesh
	glDrawElements(GL_TRIANGLES, static_cast<GLsizei>(indices.size()), GL_UNSIGNED_INT, nullptr);
	glBindVertexArray(0);
}


/******************************************
 * Sine Wave Deformed Cone Mesh
 * ---------------------------------------
 * Author: Jennifer Lakey
 *
 * This function generates a cone whose profile
 * is modulated by a sine wave along its height.
 *
 * Parameters:
 *   - baseRadius:     radius at the base of the cone
 *   - height:         total height of the cone
 *   - flattenFactor:  flattens the cone along Y
 *   - sineAmplitude:  amplitude of sine deformation
 *   - sineFrequency:  number of sine oscillations
 *   - sinePhase:      phase offset of sine wave
 *   - radialSegments: number of slices around cone
 *   - heightSegments: number of rings along height
 *
 * Geometry Process:
 *   1. Build a grid of vertices in (height x radial)
 *      parameter space.
 *   2. Apply tapering to shrink radius toward the tip.
 *   3. Apply sine deformation to the Y-component.
 *   4. Store positions and accumulate normals using
 *      weighted face normals for smooth shading.
 *   5. Normalize accumulated normals and pack final
 *      interleaved vertex data.
 *
 * The mesh is uploaded dynamically in the draw
 * function to allow the same mesh object to be
 * reused with different parameter values during
 * a single scene render. Prior to this design,
 * parameters had to be supplied in the load
 * function, which meant each parameter variation
 * required loading a separate copy of the mesh.
 ******************************************/

void ShapeMeshes::LoadSineConeMesh() {
	// Allocate VAO/VBO/EBO once; geometry is uploaded in DrawSineConeMesh().
	glGenVertexArrays(1, &m_SineConeMesh.vao);
	glGenBuffers(1, &m_SineConeMesh.vbo);
	glGenBuffers(1, &m_SineConeMesh.ebo);
}

void ShapeMeshes::DrawSineConeMesh(
	float baseRadius,
	float height,
	float flattenFactor,
	float sineAmplitude,
	float sineFrequency,
	float sinePhase,
	int radialSegments,
	int heightSegments
) {
	std::vector<GLfloat> verts;
	std::vector<GLuint> indices;
	std::vector<glm::vec3> positions;
	std::vector<glm::vec3> normals;

	// Angular and vertical increments
	float radialStep = 2.0f * 3.14159265f / radialSegments;
	float heightStep = height / heightSegments;

	// --- Generate vertex positions (no normals yet) ---
	for (int i = 0; i <= heightSegments; ++i) {
		float h = i * heightStep;       // height
		float t = static_cast<float>(i) / heightSegments;     // normalized height

		// Taper radius toward the tip
		float taper = pow(1.0f - t, 0.65f);
		float radius = baseRadius * taper;

		// Sine deformation along height
		float sineOffset = sineAmplitude * sin(sineFrequency * t * 2.0f * 3.14159265f + sinePhase);

		for (int j = 0; j <= radialSegments; ++j) {
			float theta = j * radialStep;
			float y = cos(theta);
			float z = sin(theta);

			// Radial direction around cone
			glm::vec3 radial = glm::normalize(glm::vec3(0, y, z));
			// Base offset from centerline
			glm::vec3 offset = radial * radius;

			// Apply flattening and sine deformation
			offset.y *= (1.0f - flattenFactor);
			offset.y += sineOffset;

			// Final vertex position (X = height axis)
			glm::vec3 position = glm::vec3(h, offset.y, offset.z);
			positions.push_back(position);
			normals.push_back(glm::vec3(0.0f)); // placeholder for accumulation
		}
	}

	// --- Build indices and accumulate weighted face normals ---
	for (int i = 0; i < heightSegments; ++i) {
		for (int j = 0; j < radialSegments; ++j) {
			int curr = i * (radialSegments + 1) + j;
			int next = (i + 1) * (radialSegments + 1) + j;

			int i0 = curr;
			int i1 = next;
			int i2 = curr + 1;
			int i3 = next + 1;

			glm::vec3 p0 = positions[i0];
			glm::vec3 p1 = positions[i1];
			glm::vec3 p2 = positions[i2];
			glm::vec3 p3 = positions[i3];

			// Two triangles per quad
			glm::vec3 n0 = glm::cross(p1 - p0, p2 - p0);
			glm::vec3 n1 = glm::cross(p3 - p2, p1 - p2);

			float area0 = glm::length(n0);
			float area1 = glm::length(n1);

			// Weighted accumulation for smooth shading
			normals[i0] += n0 * area0;
			normals[i1] += (n0 + n1) * 0.5f * (area0 + area1);
			normals[i2] += (n0 + n1) * 0.5f * (area0 + area1);
			normals[i3] += n1 * area1;

			// Triangle indices
			indices.push_back(i0);
			indices.push_back(i1);
			indices.push_back(i2);

			indices.push_back(i2);
			indices.push_back(i1);
			indices.push_back(i3);
		}
	}

	// --- Normalize accumulated normals and pack final vertex buffer ---
	for (size_t i = 0; i < positions.size(); ++i) {
		glm::vec3 pos = positions[i];
		glm::vec3 norm = glm::normalize(normals[i]);

		float u = static_cast<float>(i % (radialSegments + 1)) / radialSegments;
		float v = static_cast<float>(i / (radialSegments + 1)) / heightSegments;

		verts.push_back(pos.x);
		verts.push_back(pos.y);
		verts.push_back(pos.z);
		verts.push_back(norm.x);
		verts.push_back(norm.y);
		verts.push_back(norm.z);
		verts.push_back(u);
		verts.push_back(v);
	}

	// --- Upload mesh to GPU ---
	glBindVertexArray(m_SineConeMesh.vao);
	glBindBuffer(GL_ARRAY_BUFFER, m_SineConeMesh.vbo);
	glBufferData(GL_ARRAY_BUFFER, verts.size() * sizeof(GLfloat), verts.data(), GL_DYNAMIC_DRAW);

	// Vertex attribute layout: position (0), normal (1), UV (2)
	glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 8 * sizeof(GLfloat), (void*)0);
	glEnableVertexAttribArray(0);
	glVertexAttribPointer(1, 3, GL_FLOAT, GL_FALSE, 8 * sizeof(GLfloat), (void*)(3 * sizeof(GLfloat)));
	glEnableVertexAttribArray(1);
	glVertexAttribPointer(2, 2, GL_FLOAT, GL_FALSE, 8 * sizeof(GLfloat), (void*)(6 * sizeof(GLfloat)));
	glEnableVertexAttribArray(2);

	glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, m_SineConeMesh.ebo);
	glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices.size() * sizeof(GLuint), indices.data(), GL_DYNAMIC_DRAW);

	// Draw the sine-deformed cone
	glDrawElements(GL_TRIANGLES, static_cast<GLsizei>(indices.size()), GL_UNSIGNED_INT, nullptr);
	glBindVertexArray(0);
}

/******************************************
 * Superellipsoid Mesh
 * ---------------------------------------
 * Author: Jennifer Lakey
 *
 * Generates a parametric superellipsoid
 * (a member of the superquadric family)
 * defined by two independent exponents
 * that control vertical and horizontal
 * "squareness." By adjusting these
 * exponents, the shape can morph smoothly
 * between spheres, rounded cubes, sharp
 * star-like forms, and elongated solids.
 *
 * Parameters:
 *   - scaleX, scaleY, scaleZ:
 *       Axis-aligned scale factors applied
 *       after evaluating the superquadric
 *       surface. These control the final
 *       proportions of the shape.
 *
 *   - verticalExponent:
 *       Controls curvature in the latitude
 *       direction (u). Larger values create
 *       sharper vertical features; values
 *       near 1.0 approximate spherical
 *       curvature.
 *
 *   - horizontalExponent:
 *       Controls curvature in the longitude
 *       direction (v). Larger values create
 *       sharper horizontal features.
 *
 *   - uSegments:
 *       Number of subdivisions along the
 *       latitude-like direction (u).
 *
 *   - vSegments:
 *       Number of subdivisions along the
 *       longitude-like direction (v).
 *
 * Parametric Domain:
 *   u in [-PI/2, PI/2]
 *   v in [-PI, PI]
 *
 * Surface Definition:
 *   Let sgn(x) = +1 if x > 0,
 *                -1 if x < 0,
 *                 0 if x = 0.
 *
 *   Let E1 = verticalExponent
 *   Let E2 = horizontalExponent
 *
 *   x = scaleX * sgn(cos(u)) * abs(cos(u))^E1
 *                 * sgn(cos(v)) * abs(cos(v))^E2
 *
 *   y = scaleY * sgn(cos(u)) * abs(cos(u))^E1
 *                 * sgn(sin(v)) * abs(sin(v))^E2
 *
 *   z = scaleZ * sgn(sin(u)) * abs(sin(u))^E1
 *
 * Geometry Process:
 *   1. Precompute tables of cos(u), sin(u),
 *      cos(v), and sin(v) for all segment
 *      boundaries to avoid redundant trig
 *      evaluation inside the vertex loops.
 *
 *   2. For each (u, v) grid coordinate:
 *        - Apply superquadric exponentiation
 *          using sgn(x) * abs(x)^exp.
 *        - Compute the final position by
 *          applying axis scales.
 *        - Compute analytic normals using
 *          the exponentiated coordinates
 *          scaled by inverse axis lengths,
 *          then normalize.
 *        - Generate simple cylindrical UVs.
 *
 *   3. Build a triangle index buffer using
 *      a standard grid layout:
 *        (uSegments + 1) rows
 *        (vSegments + 1) columns
 *
 *   4. Upload interleaved vertex data
 *      (position, normal, UV) and index
 *      data to the GPU using GL_DYNAMIC_DRAW.
 *
 * Design Note:
 *   The mesh is generated dynamically inside
 *   DrawSuperellipsoidMesh so that a single
 *   VAO/VBO/EBO allocation (performed in
 *   LoadSuperellipsoidMesh) can be reused
 *   for any parameter combination during a
 *   single frame. This mirrors the design
 *   used for other procedural meshes in the
 *   project and avoids storing multiple
 *   static mesh variants.
 *
 * Time Complexity:
 *   O(uSegments * vSegments)
 *   Vertex generation, normal computation,
 *   and index construction all operate in
 *   constant time per grid cell.
 *
 ******************************************/

void ShapeMeshes::LoadSuperellipsoidMesh()
{
	// Allocate VAO/VBO/EBO once; data will be uploaded in DrawSuperellipsoidMesh.
	glGenVertexArrays(1, &m_SuperellipsoidMesh.vao);
	glGenBuffers(1, &m_SuperellipsoidMesh.vbo);
	glGenBuffers(1, &m_SuperellipsoidMesh.ebo);
}

void ShapeMeshes::DrawSuperellipsoidMesh(float scaleX,
	float scaleY,
	float scaleZ,
	float verticalExponent,
	float horizontalExponent,
	int   uSegments,
	int   vSegments)
{

	// --- 1. Validate and clamp parameters ---

	if (uSegments < 3) uSegments = 3;
	if (vSegments < 3) vSegments = 3;

	if (scaleX <= 0.0f) scaleX = 0.1f;
	if (scaleY <= 0.0f) scaleY = 0.1f;
	if (scaleZ <= 0.0f) scaleZ = 0.1f;

	if (verticalExponent <= 0.0f)   verticalExponent = 0.1f;
	if (horizontalExponent <= 0.0f) horizontalExponent = 0.1f;

	// Store segment count if needed later
	m_SuperellipsoidMesh.numSlices = vSegments;


	// --- 2. Precompute angle tables ---

	const float PI = 3.14159265359f;

	std::vector<float> cosU(uSegments + 1);
	std::vector<float> sinU(uSegments + 1);
	std::vector<float> cosV(vSegments + 1);
	std::vector<float> sinV(vSegments + 1);

	// u in [-PI/2, PI/2]
	for (int i = 0; i <= uSegments; ++i)
	{
		float t = static_cast<float>(i) / static_cast<float>(uSegments);
		float u = -PI * 0.5f + t * PI;
		cosU[i] = std::cos(u);
		sinU[i] = std::sin(u);
	}

	// v in [-PI, PI]
	for (int j = 0; j <= vSegments; ++j)
	{
		float t = static_cast<float>(j) / static_cast<float>(vSegments);
		float v = -PI + t * (2.0f * PI);
		cosV[j] = std::cos(v);
		sinV[j] = std::sin(v);
	}

	// --- 3. Prepare vertex/index buffers ---

	std::vector<GLfloat> verts;
	std::vector<GLuint>  indices;

	verts.reserve((uSegments + 1) * (vSegments + 1) * 8);
	indices.reserve(uSegments * vSegments * 6);

	auto sign = [](float x) -> float
	{
		return (x > 0.0f) - (x < 0.0f);
	};

	// --- 4. Generate vertices ---

	for (int i = 0; i <= uSegments; ++i)
	{
		for (int j = 0; j <= vSegments; ++j)
		{
			float cu = cosU[i];
			float su = sinU[i];
			float cv = cosV[j];
			float sv = sinV[j];

			// Apply superquadric exponents:
			// sgn(x) * abs(x)^exp
			float cu_e = sign(cu) * std::pow(std::fabs(cu), verticalExponent);
			float su_e = sign(su) * std::pow(std::fabs(su), verticalExponent);
			float cv_e = sign(cv) * std::pow(std::fabs(cv), horizontalExponent);
			float sv_e = sign(sv) * std::pow(std::fabs(sv), horizontalExponent);

			// Position on the superellipsoid surface
			float x = scaleX * cu_e * cv_e;
			float y = scaleY * cu_e * sv_e;
			float z = scaleZ * su_e;

			// Analytic normal:
			// For a superellipsoid, the normal can be derived from the
			// implicit form; here we use a scaled version of the
			// exponentiated coordinates and normalize.
			float nx = cu_e * cv_e / scaleX;
			float ny = cu_e * sv_e / scaleY;
			float nz = su_e / scaleZ;

			glm::vec3 normal(nx, ny, nz);
			normal = glm::normalize(normal);

			// UV coordinates: simple cylindrical-style mapping
			float uCoord = static_cast<float>(j) / static_cast<float>(vSegments);
			float vCoord = static_cast<float>(i) / static_cast<float>(uSegments);

			// Interleaved vertex: position (3), normal (3), UV (2)
			verts.push_back(x);
			verts.push_back(y);
			verts.push_back(z);
			verts.push_back(normal.x);
			verts.push_back(normal.y);
			verts.push_back(normal.z);
			verts.push_back(uCoord);
			verts.push_back(vCoord);
		}
	}


	// --- 5. Generate triangle indices ---

	// Grid layout: (uSegments + 1) rows, (vSegments + 1) columns
	for (int i = 0; i < uSegments; ++i)
	{
		for (int j = 0; j < vSegments; ++j)
		{
			GLuint idx0 = static_cast<GLuint>(i * (vSegments + 1) + j);
			GLuint idx1 = static_cast<GLuint>((i + 1) * (vSegments + 1) + j);
			GLuint idx2 = static_cast<GLuint>(i * (vSegments + 1) + (j + 1));
			GLuint idx3 = static_cast<GLuint>((i + 1) * (vSegments + 1) + (j + 1));

			// First triangle
			indices.push_back(idx0);
			indices.push_back(idx1);
			indices.push_back(idx2);

			// Second triangle
			indices.push_back(idx2);
			indices.push_back(idx1);
			indices.push_back(idx3);
		}
	}

	m_SuperellipsoidMesh.nVertices = static_cast<GLuint>(verts.size() / 8);
	m_SuperellipsoidMesh.nIndices = static_cast<GLuint>(indices.size());


	// --- 6. Upload to GPU and draw ---

	glBindVertexArray(m_SuperellipsoidMesh.vao);

	glBindBuffer(GL_ARRAY_BUFFER, m_SuperellipsoidMesh.vbo);
	glBufferData(GL_ARRAY_BUFFER,
		verts.size() * sizeof(GLfloat),
		verts.data(),
		GL_DYNAMIC_DRAW);

	glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 8 * sizeof(GLfloat), (void*)0);                    // position
	glEnableVertexAttribArray(0);
	glVertexAttribPointer(1, 3, GL_FLOAT, GL_FALSE, 8 * sizeof(GLfloat), (void*)(3 * sizeof(GLfloat))); // normal
	glEnableVertexAttribArray(1);
	glVertexAttribPointer(2, 2, GL_FLOAT, GL_FALSE, 8 * sizeof(GLfloat), (void*)(6 * sizeof(GLfloat))); // UV
	glEnableVertexAttribArray(2);

	glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, m_SuperellipsoidMesh.ebo);
	glBufferData(GL_ELEMENT_ARRAY_BUFFER,
		indices.size() * sizeof(GLuint),
		indices.data(),
		GL_DYNAMIC_DRAW);

	glDrawElements(GL_TRIANGLES,
		static_cast<GLsizei>(indices.size()),
		GL_UNSIGNED_INT,
		nullptr);

	glBindVertexArray(0);
}