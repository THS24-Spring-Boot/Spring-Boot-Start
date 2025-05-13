package com.example.spring_rest_intro;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {

    @Test
    void addNumbersShouldReturnSum() {

        //Arrange
        Calculator calc = new Calculator();

        //Act
        int result = calc.addNumbers(2,2);

        //Assert
       assertEquals(4, result);

    }

    @Test
    void divideNumbersShouldReturnQuotaOfAandB(){
        //Arrange
        Calculator calc = new Calculator();

        //Act
        double result = calc.divideNumbers(5,2);

        //Assert
        assertEquals(2.5, result);
    }
}