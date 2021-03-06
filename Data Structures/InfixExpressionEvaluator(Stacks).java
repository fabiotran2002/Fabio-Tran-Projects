public final class InfixExpressionEvaluator
    {

    /**
     * prevent instantiation
     */
    private InfixExpressionEvaluator()
        {
        // can't instantiate an InfixExpressionEvaluator
        }	// end constructor


    /**
     * Evaluate an infix arithmetic expression.
     *
     * @param expression
     *     an infix expression composed of unsigned decimal operands and a
     *     combination of operators ({@code +, -, *, /, %}) including parenthesized
     *     (sub-)expressions; may include spaces
     * @return the result of evaluating {@code expression}
     * @throws ArithmeticException
     *     if {@code expression} is syntactically invalid, is null/0-length, attempts
     *     to divide by zero, or contains unrecognized characters
     */
    public static long evaluate( final String expression ) throws ArithmeticException
        {

        // We'll use 2 stacks to remember what's left to evaluate
        final StackInterface<Character> operatorStack = new VectorStack<>() ;
        final StackInterface<Long> valuesStack = new VectorStack<>() ;

        // DONE

        // Checks for null expression
        if ( expression.length() == 0 )
            {
            throw new ArithmeticException( "null expression" ) ;
            }

        // Loops through each character in expression
        for ( int i = 0 ; i < expression.length() ; i++ )
            {
            char currentCharacter = expression.charAt( i ) ; // Used to improve
                                                             // clarity

            // Whitespace characters will be quietly ignored and the loop will move
            // onto next iteration
            if ( Character.isWhitespace( currentCharacter ) )
                {
                continue ;
                } // end if
            // Open parenthesis should always be pushed to operator stack
            else if ( currentCharacter == '(' )
                {
                operatorStack.push( currentCharacter ) ;
                }
            // Checks if character has a numeric value between 0-9
            else if ( Character.isDigit( currentCharacter ) )
                {
                long operand = 0 ;
                char nextCharacter = expression.charAt( i ) ;

                // Implementation for multi-digit number builder
                while ( Character.isDigit( nextCharacter ) )
                    {
                    operand = ( ( operand * 10 ) + nextCharacter ) - '0' ;
                    i++ ;

                    // Logic to stop while-loop if there are no more characters left
                    if ( i < expression.length() )
                        {
                        nextCharacter = expression.charAt( i ) ;
                        } // end if
                    else
                        {
                        break ;
                        } // end else

                    } // end while

                i-- ; // This was incremented in while-loop to check if it was a
                      // digit, but it was not therefore decrement so it can be
                      // processed through the for-loop again
                valuesStack.push( operand ) ;
                } // end else if
            // Checks if character is an operator
            else if ( ( currentCharacter == '-' ) || ( currentCharacter == '+' ) ||
                      ( currentCharacter == '*' ) || ( currentCharacter == '/' ) ||
                      ( currentCharacter == '%' ) )
                {
                // Any operator gets pushed to operator stack if it is empty
                if ( operatorStack.isEmpty() )
                    {
                    operatorStack.push( currentCharacter ) ;
                    } // end if
                // Push current character onto operator stack if open parenthesis is
                // on top of that stack
                else if ( operatorStack.peek() == '(' )
                    {
                    operatorStack.push( currentCharacter ) ;
                    } // end else if
                // If current character has lower or equal precedence, pop operator
                // stack and perform that operation with operands from popping value
                // stack twice
                else if ( precedenceOf( currentCharacter ) <=
                          ( precedenceOf( operatorStack.peek() ) ) )
                    {
                    long result = doOperation( operatorStack.pop(),
                                               valuesStack.pop(),
                                               valuesStack.pop() ) ;
                    valuesStack.push( result ) ;
                    i-- ;  // Current character wasn't dealt with because its
                           // precedence was lower so decrement so it can be
                           // processed through the loop again
                    } // end else if
                // Current character has higher precedence so push to operator stack
                else
                    {
                    operatorStack.push( currentCharacter ) ;
                    } // end else

                } // end else if
            // Checks for close parenthesis where several cases could occur
            else if ( ( currentCharacter == ')' ) )
                {
                // No other operators to compare with close parenthesis indicates
                // invalid expression
                if ( operatorStack.isEmpty() )
                    {
                    throw new ArithmeticException( "invalid expression" ) ;
                    } // end if

                // Loop to find open parenthesis from operator stack
                while ( operatorStack.peek() != '(' )
                    {
                    long result = doOperation( operatorStack.pop(),
                                               valuesStack.pop(),
                                               valuesStack.pop() ) ;
                    valuesStack.push( result ) ;

                    // If open parenthesis was not found and there are no more
                    // operators this indicates invalid expression
                    if ( operatorStack.isEmpty() )
                        {
                        throw new ArithmeticException( "invalid expression" ) ;
                        } // end if

                    } // end while

                operatorStack.pop() ; // Gets rid of open parenthesis if found

                // end else

                } // end else if
            // Any other character is illegal and ArithmeticException will be thrown
            else
                {
                throw new ArithmeticException( "unrecognized character: " + "'" +
                                               expression.charAt( i ) + "'" ) ;
                } // end else

            } // end for

        // Checks if there is any remaining unmatched open parenthesis in operator
        // stack, if
        // true will throw exception. Remaining operators gets popped and operations
        // are performed with numbers from value stack
        while ( !operatorStack.isEmpty() )
            {
            if ( ( operatorStack.peek() == '(' ) || ( operatorStack.peek() == ')' ) )
                {
                throw new ArithmeticException( "invalid expression" ) ;
                } // end if

            long b = doOperation( operatorStack.pop(),
                                  valuesStack.pop(),
                                  valuesStack.pop() ) ;
            valuesStack.push( b ) ;
            } // end while

        // Final result is on top of values stack
        return valuesStack.pop() ;

        }   // end evaluate()


    /**
     * Checks precedence of given operator. Multiplication, division, and modulus,
     * has higher precedence over addition and subtraction
     *
     * @param operator
     *     operator to check precedence of
     * @return integer 1 for lower precedence operations and integer 2 for higher
     *     precedence operations
     */
    private static int precedenceOf( char operator )
        {
        if ( ( operator == '+' ) || ( operator == '-' ) )
            {
            return 1 ;
            } // end if

        return 2 ; // evaluate method will only use precedenceOf for proper
                   // operations so simply return 2 for higher precedence operator

        } // end precedenceOf()


    /**
     * Performs operation between two given operands
     *
     * @param operator
     *     operation to be performed
     * @param secondOperand
     *     second operand
     * @param firstOperand
     *     first operand
     * @return value of type long after operation has been performed
     */
    private static long doOperation( char operator,
                                     long secondOperand,
                                     long firstOperand )
        {

        if ( operator == '-' )
            {
            return firstOperand - secondOperand ;
            } // end if
        else if ( operator == '+' )
            {
            return firstOperand + secondOperand ;
            } // end else if
        else if ( operator == '*' )
            {
            return firstOperand * secondOperand ;
            } // end else if
        else if ( operator == '/' )
            {
            // Checks for division by zero, if true exception will be thrown
            if ( secondOperand == 0 )
                {
                throw new ArithmeticException( "division by zero" ) ;
                } // end if
            return firstOperand / secondOperand ;
            } // end else if
        else // operator is %
            {
            return firstOperand % secondOperand ;
            } // end else

        } // end doOperation()

    }	// end class InfixExpressionEvaluator