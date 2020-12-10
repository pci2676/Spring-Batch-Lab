package com.javabom.bombatch.sales.job.processor;

import com.javabom.bombatch.sales.model.Sales;
import com.javabom.bombatch.sales.model.Tax;
import org.springframework.batch.item.ItemProcessor;

import java.util.Arrays;
import java.util.List;

public class ItemListProcessor implements ItemProcessor<Sales, List<Tax>> {

    @Override
    public List<Tax> process(Sales item) {
        return Arrays.asList(
                new Tax(item.getTxAmount(), item.getOwnerNo()),
                new Tax((long) (item.getTxAmount() / 1.1), item.getOwnerNo()),
                new Tax(item.getTxAmount() / 11, item.getOwnerNo())
        );
    }
}