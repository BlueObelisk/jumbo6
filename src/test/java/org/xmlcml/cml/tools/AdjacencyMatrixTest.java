package org.xmlcml.cml.tools;

import org.junit.Assert;
import org.junit.Test;

public class AdjacencyMatrixTest {

	
	@Test
	public void testAdjacencyMatrix() {
		AdjacencyMatrix adjacencyMatrix = new AdjacencyMatrix();
		Assert.assertEquals("zero", 0, adjacencyMatrix.getSize());
	}
	@Test
	public void testAdjacencyMatrixSize() {
//		AdjacencyMatrix adjacencyMatrix = new AdjacencyMatrix(atomSet);
	}

}
