-- =====================================================
-- Migration V2: Adiciona professional_id na tabela procedures
-- =====================================================

ALTER TABLE procedures ADD COLUMN professional_id UUID;

ALTER TABLE procedures
    ADD CONSTRAINT fk_procedures_professional
    FOREIGN KEY (professional_id) REFERENCES professionals(id) ON DELETE CASCADE;

CREATE INDEX idx_procedures_professional_id ON procedures(professional_id);
