package uk.ithis.mule.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.interceptor.AbstractEnvelopeInterceptor;
import org.mule.management.stats.ProcessingTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.UUID;

public class TxInterceptor extends AbstractEnvelopeInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(TxInterceptor.class);

    @Override
    public MuleEvent before(MuleEvent event) {
        boolean isExistingCorrelationId = false;

        String correlationId = event.getMessage().getCorrelationId();

        if(StringUtils.isBlank(correlationId)){
            correlationId = UUID.randomUUID().toString();
            event.getMessage().setCorrelationId(correlationId);
        } else {
            isExistingCorrelationId = true;
        }

        MDC.put("TXID",  correlationId);

        LOG.debug("Started event processing for " + event.getFlowConstruct().getName());

        if(isExistingCorrelationId){
            LOG.debug("Found existing correlation ID {}", correlationId);
        } else {
            LOG.debug("No correlation ID found, created new one {}", correlationId);
        }

        return event;
    }

    @Override
    public MuleEvent after(MuleEvent event) {
        LOG.debug("Finished event processing for " + event.getFlowConstruct().getName());
        finishProcessing(event);
        return event;
    }

    private void finishProcessing(MuleEvent event) {
        String correlationId = event.getMessage().getCorrelationId();
        if (StringUtils.isBlank(correlationId)) {
            correlationId = UUID.randomUUID().toString();

            LOG.warn("{} No correlation ID found - setting new correlation ID {}", event.getFlowConstruct().getName(), correlationId);

            event.getMessage().setCorrelationId(correlationId);
        }

        LOG.debug("{}, Copying correlation ID {} to outbound properties", event.getFlowConstruct().getName(), correlationId);

        event.getMessage().setOutboundProperty("correlationId", correlationId);
        event.getMessage().setCorrelationId(correlationId);
    }

    @Override
    public MuleEvent last(MuleEvent event, ProcessingTime time, long startTime, boolean exceptionWasThrown) throws MuleException {
        if(exceptionWasThrown){
            LOG.debug("{}, Finished event processing with exception ", event.getFlowConstruct().getName());
            finishProcessing(event);
        }
        return event;
    }
}

