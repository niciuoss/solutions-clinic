'use client'

import { useState, useEffect } from 'react';
import { useDebounce } from '@/hooks/useDebounce';
import { autocompletePatientsAction } from '@/actions/patient-actions';
import { useAuthContext } from '@/contexts/AuthContext';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
} from '@/components/ui/command';
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from '@/components/ui/popover';
import { Check, ChevronsUpDown, Loader2 } from 'lucide-react';
import { cn } from '@/lib/utils';
import type { Patient } from '@/types';

interface PatientAutocompleteProps {
  onSelect: (patient: Patient) => void;
  error?: string;
}

export function PatientAutocomplete({ onSelect, error }: PatientAutocompleteProps) {
  const { user } = useAuthContext();
  const tenantId = user?.clinicId || null;
  
  const [open, setOpen] = useState(false);
  const [search, setSearch] = useState('');
  const [patients, setPatients] = useState<Patient[]>([]);
  const [allPatients, setAllPatients] = useState<Patient[]>([]);
  const [selectedPatient, setSelectedPatient] = useState<Patient | null>(null);
  const [isInitialLoading, setIsInitialLoading] = useState(true);

  const debouncedSearch = useDebounce(search, 300);

  // Carregar todos os pacientes do tenant quando o componente montar ou o tenantId mudar
  useEffect(() => {
    const fetchAllPatients = async () => {
      if (!tenantId) {
        setAllPatients([]);
        setIsInitialLoading(false);
        return;
      }

      setIsInitialLoading(true);
      try {
        const result = await autocompletePatientsAction(tenantId);
        if (result.success && result.data) {
          setAllPatients(result.data);
        }
      } catch (error) {
        console.error('Erro ao buscar pacientes:', error);
      } finally {
        setIsInitialLoading(false);
      }
    };

    fetchAllPatients();
  }, [tenantId]);

  // Filtrar pacientes localmente baseado na busca
  useEffect(() => {
    if (!debouncedSearch || debouncedSearch.length < 2) {
      setPatients([]);
      return;
    }

    const query = debouncedSearch.toLowerCase();
    const filtered = allPatients.filter((patient) => 
      patient.fullName?.toLowerCase().includes(query) ||
      patient.cpf?.includes(query) ||
      patient.phone?.includes(query)
    );

    setPatients(filtered);
  }, [debouncedSearch, allPatients]);

  const handleSelect = (patient: Patient) => {
    setSelectedPatient(patient);
    onSelect(patient);
    setOpen(false);
  };

  return (
    <div className="space-y-2">
      <Popover open={open} onOpenChange={setOpen}>
        <PopoverTrigger asChild>
          <Button
            variant="outline"
            role="combobox"
            aria-expanded={open}
            className={cn(
              'w-full justify-between',
              error && 'border-red-500'
            )}
          >
            {selectedPatient ? selectedPatient.fullName : 'Selecione um paciente'}
            <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
          </Button>
        </PopoverTrigger>
        <PopoverContent className="w-full p-0" align="start">
          <Command>
            <CommandInput
              placeholder="Digite o nome do paciente..."
              value={search}
              onValueChange={setSearch}
            />
            <CommandList>
              {isInitialLoading ? (
                <div className="flex items-center justify-center p-4">
                  <Loader2 className="h-4 w-4 animate-spin" />
                  <span className="ml-2 text-sm text-muted-foreground">Carregando pacientes...</span>
                </div>
              ) : !tenantId ? (
                <div className="p-4 text-sm text-muted-foreground text-center">
                  Clínica não identificada. Faça login novamente.
                </div>
              ) : (
                <>
                  <CommandEmpty>
                    {search.length < 2
                      ? 'Digite pelo menos 2 caracteres para buscar'
                      : 'Nenhum paciente encontrado'}
                  </CommandEmpty>
                  <CommandGroup>
                    {patients.map((patient) => (
                      <CommandItem
                        key={patient.id}
                        value={patient.fullName}
                        onSelect={() => handleSelect(patient)}
                      >
                        <Check
                          className={cn(
                            'mr-2 h-4 w-4',
                            selectedPatient?.id === patient.id
                              ? 'opacity-100'
                              : 'opacity-0'
                          )}
                        />
                        <div className="flex flex-col">
                          <span>{patient.fullName}</span>
                          <span className="text-xs text-muted-foreground">
                            {patient.cpf && `CPF: ${patient.cpf}`}
                            {patient.phone && ` • Tel: ${patient.phone}`}
                          </span>
                        </div>
                      </CommandItem>
                    ))}
                  </CommandGroup>
                </>
              )}
            </CommandList>
          </Command>
        </PopoverContent>
      </Popover>
      {error && <p className="text-sm text-red-500">{error}</p>}
    </div>
  );
}