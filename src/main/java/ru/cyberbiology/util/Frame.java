package ru.cyberbiology.util;

import java.io.DataOutputStream;
import java.io.IOException;
import ru.cyberbiology.Bot;

public interface Frame {

    public void addBot(Bot bot);

    public int save(DataOutputStream fileout) throws IOException;

}