package com.aayushatharva.sourcecenginequerycacher.config.parsers;

import org.apache.commons.cli.CommandLine;

public class CmdValueParser implements ValueParser {
    private CommandLine cmd;

    public CmdValueParser(CommandLine cmd) {
        this.cmd = cmd;
    }

    @Override
    public String getValue(String key) {
        return cmd.getOptionValue(key);
    }

    @Override
    public boolean hasKey(String key) {
        return cmd.hasOption(key);
    }
}
