package com.example.event_manager.repository;
import com.example.event_manager.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {}
