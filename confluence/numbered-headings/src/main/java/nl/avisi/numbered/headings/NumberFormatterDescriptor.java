package nl.avisi.numbered.headings;

import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;

import nl.avisi.numbered.headings.format.NumberingFormat;

public class NumberFormatterDescriptor extends AbstractModuleDescriptor<NumberingFormat> {

    public NumberFormatterDescriptor(final ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    @Override
    public NumberingFormat getModule() {
        return moduleFactory.createModule(moduleClassName, this);
    }
}
