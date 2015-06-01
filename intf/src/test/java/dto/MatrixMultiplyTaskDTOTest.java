package dto;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class MatrixMultiplyTaskDTOTest {
    
    @org.junit.Test
    public void shouldMarshallAndUnMarshall() throws Exception {
        Integer[][] A = {
                {0, 1},
                {0, 0}
        };
        int magicNumber = 42;
        MatrixMultiplyTaskDTO dto = new MatrixMultiplyTaskDTO(A, A, magicNumber, magicNumber, magicNumber, ExperimentStrategy.MIN_MIN);
        String taskJson = new ObjectMapper().writeValueAsString(dto);
        
        MatrixMultiplyTaskDTO unMarshalled = new ObjectMapper().readValue(taskJson, MatrixMultiplyTaskDTO.class);
        assertEquals(ExperimentStrategy.MIN_MIN, unMarshalled.getExperimentStrategy());
        assertArrayEquals(A, unMarshalled.getA());
        assertArrayEquals(A, unMarshalled.getA());
        assertEquals(magicNumber, unMarshalled.getClientNumber());
        assertEquals(magicNumber, unMarshalled.getVerticalBlockNum());
        assertEquals(magicNumber, unMarshalled.getHorizontalBlockNum());
    }
}