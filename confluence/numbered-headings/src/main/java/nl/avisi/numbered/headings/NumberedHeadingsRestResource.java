package nl.avisi.numbered.headings;

import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.List;

/**
 * General REST resource for Numbered Headings
 */
@Path("/formatter")
public class NumberedHeadingsRestResource {

    private final NumberFormatterManager numberFormatterManager;

    public NumberedHeadingsRestResource(final NumberFormatterManager numberFormatterManager) {
        this.numberFormatterManager = numberFormatterManager;
    }

    /**
     * Returns all enabled numbering formatters as a JSON array.
     *
     * @return a 200 HTTP response with the enabled numbering formatters as in its body.
     */
    @GET
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getEnabledFormatters() {
        List<String> numberFormatterDescriptorNames = numberFormatterManager.getNumberFormatterDescriptorNames();

        return Response.ok(numberFormatterDescriptorNames).build();
    }
}
