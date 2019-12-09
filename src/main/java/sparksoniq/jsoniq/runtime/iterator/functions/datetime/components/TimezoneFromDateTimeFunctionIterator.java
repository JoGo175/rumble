package sparksoniq.jsoniq.runtime.iterator.functions.datetime.components;

import org.joda.time.Period;
import org.rumbledb.api.Item;
import sparksoniq.exceptions.IteratorFlowException;
import sparksoniq.jsoniq.item.ItemFactory;
import sparksoniq.jsoniq.runtime.iterator.RuntimeIterator;
import sparksoniq.jsoniq.runtime.iterator.functions.base.LocalFunctionCallIterator;
import sparksoniq.jsoniq.runtime.metadata.IteratorMetadata;
import sparksoniq.semantics.DynamicContext;

import java.util.List;

public class TimezoneFromDateTimeFunctionIterator extends LocalFunctionCallIterator {

    private static final long serialVersionUID = 1L;
    private Item _dateTimeItem = null;

    public TimezoneFromDateTimeFunctionIterator(
            List<RuntimeIterator> arguments,
            IteratorMetadata iteratorMetadata
    ) {
        super(arguments, iteratorMetadata);
    }

    @Override
    public Item next() {
        if (this._hasNext) {
            this._hasNext = false;
            return ItemFactory.getInstance()
                .createDayTimeDurationItem(
                    new Period(_dateTimeItem.getDateTimeValue().getZone().toTimeZone().getRawOffset())
                );
        } else
            throw new IteratorFlowException(
                    RuntimeIterator.FLOW_EXCEPTION_MESSAGE + " timezone-from-dateTime function",
                    getMetadata()
            );
    }

    @Override
    public void open(DynamicContext context) {
        super.open(context);
        _dateTimeItem = this.getSingleItemFromIterator(
            this._children.get(0)
        );
        this._hasNext = _dateTimeItem != null && _dateTimeItem.hasTimeZone();
    }
}