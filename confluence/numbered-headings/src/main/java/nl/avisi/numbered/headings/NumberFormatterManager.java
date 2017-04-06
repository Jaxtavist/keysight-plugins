package nl.avisi.numbered.headings;

import com.atlassian.plugin.PluginAccessor;
import org.springframework.stereotype.Component;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import org.springframework.beans.factory.annotation.Autowired;

import nl.avisi.numbered.headings.format.DecimalNumberingFormat;
import nl.avisi.numbered.headings.format.NumberingFormat;

import java.util.ArrayList;
import java.util.List;

@Component
public class NumberFormatterManager {

    private final PluginAccessor pluginAccessor;

    @Autowired
    public NumberFormatterManager(final PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    public NumberingFormat fromString(final String numberFormatString) {
        // Do not cache this, modules can be enabled/disabled at any time...
        List<NumberFormatterDescriptor> numberFormatterDescriptors = pluginAccessor.getEnabledModuleDescriptorsByClass(NumberFormatterDescriptor.class);

        for (NumberFormatterDescriptor numberFormatterDescriptor : numberFormatterDescriptors) {
            if (numberFormatterDescriptor.getName().equals(numberFormatString)) {
                return numberFormatterDescriptor.getModule();
            }
        }

        return new DecimalNumberingFormat();
    }

    public List<String> getNumberFormatterDescriptorNames() {
        List<NumberFormatterDescriptor> numberFormatterDescriptors = pluginAccessor.getEnabledModuleDescriptorsByClass(NumberFormatterDescriptor.class);
        List<String> numberFormatterDescriptorNames = new ArrayList<String>(numberFormatterDescriptors.size());

        for (NumberFormatterDescriptor numberFormatterDescriptor : numberFormatterDescriptors) {
            numberFormatterDescriptorNames.add(numberFormatterDescriptor.getName());
        }

        return numberFormatterDescriptorNames;
    }
}
