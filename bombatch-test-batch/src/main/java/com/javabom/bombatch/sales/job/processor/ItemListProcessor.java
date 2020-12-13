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
                new Tax(item.getAmount(), item.getOwnerNo()),
                new Tax((long) (item.getAmount() / 1.1), item.getOwnerNo()),
                new Tax(item.getAmount() / 11, item.getOwnerNo())
        );
    }
}