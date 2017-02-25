package nl.avisi.numbered.headings;

import com.atlassian.plugin.spring.scanner.annotation.export.ModuleType;
import org.springframework.stereotype.Component;

import com.atlassian.plugin.osgi.external.ListableModuleDescriptorFactory;
import com.atlassian.plugin.osgi.external.SingleModuleDescriptorFactory;
import com.atlassian.plugin.hostcontainer.HostContainer;
import org.springframework.beans.factory.annotation.Autowired;

@ModuleType(ListableModuleDescriptorFactory.class)
@Component
public class NumberFormatterDescriptorFactory extends SingleModuleDescriptorFactory<NumberFormatterDescriptor>
{
    @Autowired
    public NumberFormatterDescriptorFactory(final HostContainer hostContainer)
    {
        super(hostContainer, "number-formatter", NumberFormatterDescriptor.class);
    }
}
