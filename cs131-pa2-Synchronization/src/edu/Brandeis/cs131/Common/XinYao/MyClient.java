package edu.Brandeis.cs131.Common.XinYao;

import java.util.Random;

import edu.Brandeis.cs131.Common.Abstract.Client;
import edu.Brandeis.cs131.Common.Abstract.Industry;

public abstract class MyClient extends Client {

    public MyClient(String name, Industry industry) {
        super(name, industry, new Random().nextInt(10), 3);
    }
    
}
