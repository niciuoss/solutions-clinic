'use client'

import { useState } from 'react';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogFooter,
} from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { ClipboardList } from 'lucide-react';
import type { VitalSigns } from '@/types';

interface TriageDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onConfirm: (data: VitalSigns | null) => void;
}

export function TriageDialog({ open, onOpenChange, onConfirm }: TriageDialogProps) {
  const [step, setStep] = useState<'ask' | 'form'>('ask');
  const [formData, setFormData] = useState<VitalSigns>({
    bloodPressure: '',
    heartRate: 0,
    temperature: 0,
    oxygenSaturation: 0,
    weight: 0,
    height: 0,
  });

  const handleOpenChange = (value: boolean) => {
    if (!value) {
      setStep('ask');
      setFormData({
        bloodPressure: '',
        heartRate: 0,
        temperature: 0,
        oxygenSaturation: 0,
        weight: 0,
        height: 0,
      });
    }
    onOpenChange(value);
  };

  const handleNo = () => {
    onConfirm(null);
    handleOpenChange(false);
  };

  const handleYes = () => {
    setStep('form');
  };

  const handleSave = () => {
    onConfirm(formData);
    handleOpenChange(false);
  };

  return (
    <Dialog open={open} onOpenChange={handleOpenChange}>
      <DialogContent className={step === 'form' ? 'sm:max-w-lg' : 'sm:max-w-md'}>
        {step === 'ask' ? (
          <>
            <DialogHeader>
              <DialogTitle className="flex items-center gap-2">
                <ClipboardList className="h-5 w-5" />
                Triagem do Paciente
              </DialogTitle>
              <DialogDescription>
                Deseja cadastrar os dados de triagem do paciente antes de iniciar o atendimento?
              </DialogDescription>
            </DialogHeader>
            <DialogFooter className="flex gap-2 sm:justify-end">
              <Button variant="outline" onClick={handleNo}>
                Nao, ir ao prontuario
              </Button>
              <Button onClick={handleYes}>
                Sim, cadastrar triagem
              </Button>
            </DialogFooter>
          </>
        ) : (
          <>
            <DialogHeader>
              <DialogTitle className="flex items-center gap-2">
                <ClipboardList className="h-5 w-5" />
                Dados de Triagem
              </DialogTitle>
              <DialogDescription>
                Preencha os sinais vitais do paciente.
              </DialogDescription>
            </DialogHeader>
            <div className="grid gap-4 py-4">
              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label>Pressao Arterial (mmHg)</Label>
                  <Input
                    placeholder="120/80"
                    value={formData.bloodPressure}
                    onChange={(e) =>
                      setFormData({ ...formData, bloodPressure: e.target.value })
                    }
                  />
                </div>
                <div className="space-y-2">
                  <Label>Freq. Cardiaca (bpm)</Label>
                  <Input
                    type="number"
                    placeholder="72"
                    value={formData.heartRate || ''}
                    onChange={(e) =>
                      setFormData({ ...formData, heartRate: Number(e.target.value) })
                    }
                  />
                </div>
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label>Temperatura (°C)</Label>
                  <Input
                    type="number"
                    step="0.1"
                    placeholder="36.5"
                    value={formData.temperature || ''}
                    onChange={(e) =>
                      setFormData({ ...formData, temperature: Number(e.target.value) })
                    }
                  />
                </div>
                <div className="space-y-2">
                  <Label>Saturacao O₂ (%)</Label>
                  <Input
                    type="number"
                    placeholder="98"
                    value={formData.oxygenSaturation || ''}
                    onChange={(e) =>
                      setFormData({ ...formData, oxygenSaturation: Number(e.target.value) })
                    }
                  />
                </div>
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label>Peso (kg)</Label>
                  <Input
                    type="number"
                    step="0.1"
                    placeholder="68"
                    value={formData.weight || ''}
                    onChange={(e) =>
                      setFormData({ ...formData, weight: Number(e.target.value) })
                    }
                  />
                </div>
                <div className="space-y-2">
                  <Label>Altura (cm)</Label>
                  <Input
                    type="number"
                    placeholder="165"
                    value={formData.height || ''}
                    onChange={(e) =>
                      setFormData({ ...formData, height: Number(e.target.value) })
                    }
                  />
                </div>
              </div>
            </div>
            <DialogFooter className="flex gap-2 sm:justify-end">
              <Button variant="outline" onClick={() => setStep('ask')}>
                Voltar
              </Button>
              <Button onClick={handleSave}>
                Salvar e continuar
              </Button>
            </DialogFooter>
          </>
        )}
      </DialogContent>
    </Dialog>
  );
}
