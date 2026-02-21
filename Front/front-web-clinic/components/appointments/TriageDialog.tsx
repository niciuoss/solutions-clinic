'use client'

import { useState, useEffect } from 'react';
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
  initialData?: VitalSigns;
}

const emptyFormData: VitalSigns = {
  bloodPressure: '',
  heartRate: 0,
  temperature: 0,
  oxygenSaturation: 0,
  weight: 0,
  height: 0,
};

export function TriageDialog({ open, onOpenChange, onConfirm, initialData }: TriageDialogProps) {
  const [formData, setFormData] = useState<VitalSigns>(emptyFormData);

  useEffect(() => {
    if (open) {
      setFormData(initialData && Object.keys(initialData).length > 0 ? { ...emptyFormData, ...initialData } : emptyFormData);
    }
  }, [open, initialData]);

  const handleOpenChange = (value: boolean) => {
    onOpenChange(value);
  };

  const handleSave = () => {
    onConfirm(formData);
    handleOpenChange(false);
  };

  return (
    <Dialog open={open} onOpenChange={handleOpenChange}>
      <DialogContent className="sm:max-w-lg">
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
          <Button variant="outline" onClick={() => handleOpenChange(false)}>
            Cancelar
          </Button>
          <Button onClick={handleSave}>
            Salvar Triagem
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
