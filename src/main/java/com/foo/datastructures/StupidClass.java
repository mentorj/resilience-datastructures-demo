package com.foo.datastructures;

public class StupidClass {
    private boolean completion;
    public String alwaysFail(){
        throw new RuntimeException("Boom");
    }

    public void toggleCompletion(){
        completion=true;
    }

    public boolean isCompletion() {
        return completion;
    }
}
