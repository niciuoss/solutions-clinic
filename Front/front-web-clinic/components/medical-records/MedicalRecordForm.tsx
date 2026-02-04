'use client'

import { useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Textarea } from '@/components/ui/textarea';
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from '@/components/ui/accordion';
import { Mic, Save } from 'lucide-react';
import { toast } from 'sonner';

interface MedicalRecordFormProps {
  appointmentId: string;
}

export function MedicalRecordForm({ appointmentId }: MedicalRecordFormProps) {
  const [formData, setFormData] = useState({
    chiefComplaint: '',
    historyOfPresentIllness: '',
    physicalExamination: '',
    diagnosticHypothesis: '',
    treatmentPlan: '',
    prescriptions: '',
    observations: '',
  });

  const handleSave = () => {
    // TODO: Implementar salvamento
    toast.success('Prontuário salvo com sucesso!');
  };

  const handleDictate = () => {
    // TODO: Implementar ditado por voz
    toast.info('Função de ditado em desenvolvimento');
  };

  return (
    <Card>
      <CardHeader className="flex flex-row items-center justify-between">
        <CardTitle>Prontuário</CardTitle>
        <div className="flex items-center gap-2">
          <Button variant="outline" size="sm" onClick={handleDictate}>
            <Mic className="mr-2 h-4 w-4" />
            Ditar
          </Button>
          <Button size="sm" onClick={handleSave}>
            <Save className="mr-2 h-4 w-4" />
            Salvar
          </Button>
        </div>
      </CardHeader>
      <CardContent>
        <Accordion type="multiple" className="space-y-4" defaultValue={['item-1']}>
          {/* Queixa Principal */}
          <AccordionItem value="item-1" className="border rounded-lg px-4">
            <AccordionTrigger className="hover:no-underline">
              Queixa Principal
            </AccordionTrigger>
            <AccordionContent>
              <Textarea
                placeholder="Descreva a queixa principal do paciente..."
                rows={4}
                value={formData.chiefComplaint}
                onChange={(e) =>
                  setFormData({ ...formData, chiefComplaint: e.target.value })
                }
                className="resize-none"
              />
            </AccordionContent>
          </AccordionItem>

          {/* História da Doença Atual */}
          <AccordionItem value="item-2" className="border rounded-lg px-4">
            <AccordionTrigger className="hover:no-underline">
              História da Doença Atual
            </AccordionTrigger>
            <AccordionContent>
              <Textarea
                placeholder="Descreva a evolução dos sintomas, fatores de melhora/piora..."
                rows={4}
                value={formData.historyOfPresentIllness}
                onChange={(e) =>
                  setFormData({ ...formData, historyOfPresentIllness: e.target.value })
                }
                className="resize-none"
              />
            </AccordionContent>
          </AccordionItem>

          {/* Exame Físico */}
          <AccordionItem value="item-3" className="border rounded-lg px-4">
            <AccordionTrigger className="hover:no-underline">
              Exame Físico
            </AccordionTrigger>
            <AccordionContent>
              <Textarea
                placeholder="Registre os achados do exame físico..."
                rows={4}
                value={formData.physicalExamination}
                onChange={(e) =>
                  setFormData({ ...formData, physicalExamination: e.target.value })
                }
                className="resize-none"
              />
            </AccordionContent>
          </AccordionItem>

          {/* Hipótese Diagnóstica */}
          <AccordionItem value="item-4" className="border rounded-lg px-4">
            <AccordionTrigger className="hover:no-underline">
              Hipótese Diagnóstica
            </AccordionTrigger>
            <AccordionContent>
              <Textarea
                placeholder="Descreva as hipóteses diagnósticas..."
                rows={4}
                value={formData.diagnosticHypothesis}
                onChange={(e) =>
                  setFormData({ ...formData, diagnosticHypothesis: e.target.value })
                }
                className="resize-none"
              />
            </AccordionContent>
          </AccordionItem>

          {/* Plano de Tratamento */}
          <AccordionItem value="item-5" className="border rounded-lg px-4">
            <AccordionTrigger className="hover:no-underline">
              Plano de Tratamento
            </AccordionTrigger>
            <AccordionContent>
              <Textarea
                placeholder="Descreva o plano terapêutico..."
                rows={4}
                value={formData.treatmentPlan}
                onChange={(e) =>
                  setFormData({ ...formData, treatmentPlan: e.target.value })
                }
                className="resize-none"
              />
            </AccordionContent>
          </AccordionItem>

          {/* Prescrições */}
          <AccordionItem value="item-6" className="border rounded-lg px-4">
            <AccordionTrigger className="hover:no-underline">
              Prescrições
            </AccordionTrigger>
            <AccordionContent>
              <Textarea
                placeholder="Liste os medicamentos prescritos..."
                rows={4}
                value={formData.prescriptions}
                onChange={(e) =>
                  setFormData({ ...formData, prescriptions: e.target.value })
                }
                className="resize-none"
              />
            </AccordionContent>
          </AccordionItem>

          {/* Observações */}
          <AccordionItem value="item-7" className="border rounded-lg px-4">
            <AccordionTrigger className="hover:no-underline">
              Observações
            </AccordionTrigger>
            <AccordionContent>
              <Textarea
                placeholder="Observações gerais..."
                rows={4}
                value={formData.observations}
                onChange={(e) =>
                  setFormData({ ...formData, observations: e.target.value })
                }
                className="resize-none"
              />
            </AccordionContent>
          </AccordionItem>
        </Accordion>
      </CardContent>
    </Card>
  );
}