package com.zerobase.storeapi.service;

import com.zerobase.storeapi.client.MemberClient;
import com.zerobase.storeapi.client.from.HeartForm;
import com.zerobase.storeapi.client.from.ItemsForm;
import com.zerobase.storeapi.domain.dto.ItemDto;
import com.zerobase.storeapi.domain.entity.Item;
import com.zerobase.storeapi.domain.entity.Option;
import com.zerobase.storeapi.domain.entity.Store;
import com.zerobase.storeapi.domain.form.item.CreateItem;
import com.zerobase.storeapi.domain.form.item.DeleteItem;
import com.zerobase.storeapi.domain.form.item.UpdateItem;
import com.zerobase.storeapi.domain.form.option.CreateOption;
import com.zerobase.storeapi.exception.StoreException;
import com.zerobase.storeapi.repository.StoreItemRepository;
import com.zerobase.storeapi.repository.StoreItemOptionRepository;
import com.zerobase.storeapi.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.zerobase.storeapi.exception.ErrorCode.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class StoreItemService {
    private final StoreRepository storeRepository;
    private final StoreItemRepository storeItemRepository;
    private final StoreItemOptionRepository storeItemOptionRepository;
    private final MemberClient memberClient;

    public ItemDto createItem(Long sellerId, CreateItem form) {
        Store store = checkMatchStoreAndSeller(form.getStoreId(), sellerId);

        checkItemName(store.getId(), form.getName());

        checkOptionName(form.getOptions());

        checkOptionPrice(form.getOptions());
        checkOptionQuantity(form.getOptions());

        Item item = Item.of(sellerId, form, store.getName());
        storeItemRepository.save(item);
        return ItemDto.from(item);
    }

    private void checkOptionQuantity(List<CreateOption> options) {
        options.forEach(option -> {
            if (option.getQuantity() <= 0) {
                String errorDescription = String.format("[%s] 수량 : %s", option.getName(), option.getQuantity());
                throw new StoreException(CHECK_OPTION_QUANTITY, errorDescription);
            }
        });
    }

    private Store checkMatchStoreAndSeller(Long storeId, Long sellerId) {
        return storeRepository.findByIdAndSellerId(storeId, sellerId)
                .orElseThrow(() -> new StoreException(UNMATCHED_SELLER_STORE));
    }

    private void checkOptionPrice(List<CreateOption> options) {
        options.forEach(option -> {
            if (option.getPrice() <= 0) {
                String errorDescription = String.format("[%s] 가격 : %s", option.getName(), option.getPrice());
                throw new StoreException(CHECK_OPTION_PRICE, errorDescription);
            }
        });
    }

    private void checkOptionName(List<CreateOption> options) {
        List<String> optionNames = options.stream().map(CreateOption::getName).collect(Collectors.toList());
        if (optionNames.size() != Set.copyOf(optionNames).size()) {
            throw new StoreException(DUPLICATE_OPTION_NAME);
        }
    }

    private void checkItemName(Long storeId, String itemName) {
        if (storeItemRepository.existsByStoreIdAndName(storeId, itemName)) {
            throw new StoreException(DUPLICATE_ITEM_NAME);
        }
    }

    @Transactional
    public ItemDto updateItem(Long sellerId, UpdateItem form) {
        Store store = checkMatchStoreAndSeller(form.getStoreId(), sellerId);

        if (storeItemRepository.existsByStoreIdAndNameAndIdNot(store.getId(), form.getName(), form.getId())) {
            throw new StoreException(DUPLICATE_ITEM_NAME);
        }

        // 아이템 내용 업데이트
        Item item = storeItemRepository.findById(form.getId())
                .orElseThrow(() -> new StoreException(NOT_FOUND_ITEM));
        item.updateItem(form);

        // 옵션 업데이트 따로 안만들고 같이 진행
        // 옵션 이름 중복확인
        checkOptionName(form.getOptions());

        // 옵션 가격>0 확인
        checkOptionPrice(form.getOptions());

        // 기존 아이템의 옵션들 삭제
        storeItemOptionRepository.deleteAllByItemId(form.getId());

        // 새로운 옵션 만들어 아이템에 추가
        List<Option> newOptions = form.getOptions().stream()
                .map(Option::of)
                .collect(Collectors.toList());
        item.updateOptions(newOptions);

        // 옵션 테이블에 추가
        storeItemOptionRepository.saveAll(newOptions);

        return ItemDto.from(item);
    }

    public DeleteItem deleteItem(Long sellerId, Long id) {
        Item item = storeItemRepository.findById(id)
                .orElseThrow(() -> new StoreException(NOT_FOUND_ITEM));

        checkMatchStoreAndSeller(item.getStoreId(), sellerId);

        storeItemRepository.delete(item);

        memberClient.deleteHeartItem(id);

        return DeleteItem.builder()
                .itemName(item.getName())
                .deletedAt(LocalDateTime.now())
                .build();
    }

    @Transactional
    public boolean increaseHeart(HeartForm form) {
        try {
            Item item = storeItemRepository.findById(form.getItemId())
                    .orElseThrow(() -> new StoreException(NOT_FOUND_ITEM));
            item.increaseHeart();
            return true;
        } catch (StoreException e) {
            return false;
        }
    }

    @Transactional
    public boolean decreaseHeart(HeartForm form) {
        try {
            Item item = storeItemRepository.findById(form.getItemId())
                    .orElseThrow(() -> new StoreException(NOT_FOUND_ITEM));
            item.decreaseHeart();
            return true;
        } catch (StoreException e) {
            return false;
        }
    }

    public Page<ItemDto> getItems(ItemsForm form, Pageable pageable) {
        // itemDto에서 옵션없이 넘기도록 수정
        return storeItemRepository.findAllByIdIn(form.getHeartList(), pageable)
                .map(ItemDto::from);
    }
}
