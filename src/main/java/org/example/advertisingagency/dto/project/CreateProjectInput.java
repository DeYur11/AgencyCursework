package org.example.advertisingagency.dto.project;

import lombok.Data;


@Data
public class CreateProjectInput {
    private Integer clientId; // Ідентифікатор клієнта
    private Integer projectTypeId; // Ідентифікатор типу проекту
    private String name; // Назва проекту
    private String description; // Опис проекту
    private Float cost; // Вартість проекту
    private Float estimateCost; // Оцінена вартість проекту
    private String paymentDeadline; // Термін оплати
    private Integer managerId; // Ідентифікатор менеджера (необов'язкове поле)
}
